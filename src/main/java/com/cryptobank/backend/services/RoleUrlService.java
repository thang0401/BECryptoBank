package com.cryptobank.backend.services;

import com.cryptobank.backend.DTO.RoleUrlDTO;
import com.cryptobank.backend.DTO.request.RoleUrlCreateRequest;
import com.cryptobank.backend.DTO.request.RoleUrlUpdateRequest;
import com.cryptobank.backend.entity.RoleUrl;
import com.cryptobank.backend.exception.AlreadyExistException;
import com.cryptobank.backend.exception.ResourceNotFoundException;
import com.cryptobank.backend.mapper.RoleUrlMapper;
import com.cryptobank.backend.repository.RoleUrlDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RoleUrlService {

    private final RoleUrlDAO dao;
    private final RoleService roleService;
    private final RoleUrlMapper mapper;

    public Page<RoleUrlDTO> getAll(Pageable pageable) {
        return dao.findAll(ignoreDeleted(), pageable).map(mapper::toResponse);
    }

    public RoleUrlDTO toResponseFromId(String id) {
        RoleUrl role = getById(id);
        return role == null ? null : mapper.toResponse(role);
    }

    public RoleUrl getById(String id) {
        return dao.findOne(ignoreDeleted()
                .and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("id"), id)))
            .orElseThrow(() -> new ResourceNotFoundException("Role url with id " + id + " not found"));
    }

    public RoleUrl getByRoleAndUrl(String role, String url) {
        return dao.findByRoleIdAndFunctionUrl(role, url);
    }

    public RoleUrlDTO save(RoleUrlCreateRequest request) {
        roleService.getById(request.getRole());
        RoleUrl found = getByRoleAndUrl(request.getRole(), request.getUrl());
        if (found != null && !found.getDeleted()) {
            throw new AlreadyExistException("Role " + request.getRole() + " with url " + request.getUrl() + " already exist");
        }
        RoleUrl roleUrl = mapper.fromCreateRequest(request);
        return mapper.toResponse(dao.save(roleUrl));
    }

    public RoleUrlDTO update(String id, RoleUrlUpdateRequest request) {
        roleService.getById(request.getRole());
        RoleUrl found = getById(id);
        RoleUrl updated = mapper.fromUpdateRequest(found, request);
        return mapper.toResponse(dao.save(updated));
    }

    public boolean delete(String id) {
        RoleUrl roleUrl = getById(id);
        if (roleUrl != null) {
            roleUrl.setDeleted(true);
            dao.save(roleUrl);
            return true;
        }
        return false;
    }

    private Specification<RoleUrl> ignoreDeleted() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get("deleted"), true);
    }

}
