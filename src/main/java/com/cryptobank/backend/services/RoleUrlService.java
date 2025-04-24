package com.cryptobank.backend.services;

import com.cryptobank.backend.DTO.RoleUrlDTO;
import com.cryptobank.backend.DTO.request.RoleUrlCreateRequest;
import com.cryptobank.backend.DTO.request.RoleUrlUpdateRequest;
import com.cryptobank.backend.entity.Role;
import com.cryptobank.backend.entity.RoleUrl;
import com.cryptobank.backend.exception.AlreadyExistException;
import com.cryptobank.backend.exception.ResourceNotFoundException;
import com.cryptobank.backend.mapper.RoleUrlMapper;
import com.cryptobank.backend.repository.RoleUrlDAO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RoleUrlService {

    private final RoleUrlDAO dao;
    private final RoleService roleService;
    private final RoleUrlMapper mapper;

    public List<RoleUrlDTO> getAll(String roleId) {
        Specification<RoleUrl> spec = ignoreDeleted();
        if (roleId != null && !roleId.isBlank())
            spec = spec.and((root, query, cb) -> cb.equal(root.get("role").get("id"), roleId));
        return dao.findAll(spec).stream().map(mapper::toDTO).toList();
    }

    public RoleUrlDTO toResponseFromId(String id) {
        RoleUrl role = getById(id);
        return role == null ? null : mapper.toDTO(role);
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
        Role role = roleService.getById(request.getRoleId());
        RoleUrl found = getByRoleAndUrl(request.getRoleId(), request.getUrl());
        if (found != null && !found.getDeleted()) {
            throw new AlreadyExistException("Role " + request.getRoleId() + " with url " + request.getUrl() + " already exist");
        }
        RoleUrl roleUrl = mapper.fromCreateRequest(request);
        roleUrl.setRole(role);
        return mapper.toDTO(dao.save(roleUrl));
    }

    public RoleUrlDTO update(String id, RoleUrlUpdateRequest request) {
        Role role = roleService.getById(request.getRoleId());
        RoleUrl found = getById(id);
        RoleUrl updated = mapper.fromUpdateRequest(found, request);
        updated.setRole(role);
        return mapper.toDTO(dao.save(updated));
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
