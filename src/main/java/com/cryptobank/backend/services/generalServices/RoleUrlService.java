package com.cryptobank.backend.services.generalServices;

import com.cryptobank.backend.DTO.RoleDTO;
import com.cryptobank.backend.DTO.RoleUrlDTO;
import com.cryptobank.backend.DTO.request.RoleUrlCreateRequest;
import com.cryptobank.backend.DTO.request.RoleUrlUpdateRequest;
import com.cryptobank.backend.entity.RoleUrl;
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
    private final RoleUrlMapper mapper;

    public Page<RoleUrlDTO> getAll(Pageable pageable) {
        return dao.findAll(ignoreDeleted(), pageable).map(mapper::toResponse);
    }

    public RoleUrlDTO toResponseFromId(String id) {
        RoleUrl role = getById(id);
        return role == null ? null : mapper.toResponse(role);
    }

    public RoleUrlDTO toResponseFromUrl(String url) {
        RoleUrl role = getByUrl(url);
        return role == null ? null : mapper.toResponse(role);
    }

    public RoleUrl getById(String id) {
        return dao.findOne(ignoreDeleted()
                .and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("id"), id))).orElse(null);
    }

    public RoleUrl getByUrl(String url) {
        return dao.findOne(ignoreDeleted()
                .and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("functionUrl"), url))).orElse(null);
    }

    public RoleUrl getByRoleAndUrl(String role, String url) {
        return dao.findOne(ignoreDeleted()
                .and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("role").get("id"), role))
                .and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("functionUrl"), url))).orElse(null);
    }

    public RoleUrlDTO save(RoleUrlCreateRequest request) {
        RoleUrl found = getByRoleAndUrl(request.getRole(), request.getUrl());
        if (found != null) {
            found.setDeleted(false);
            return mapper.toResponse(dao.save(found));
        }
        RoleUrl roleUrl = mapper.fromCreateRequest(request);
        return mapper.toResponse(dao.save(roleUrl));
    }

    public RoleUrlDTO update(String id, RoleUrlUpdateRequest request) {
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
