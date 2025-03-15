package com.cryptobank.backend.services.generalServices;

import com.cryptobank.backend.DTO.RoleUrlCreateRequest;
import com.cryptobank.backend.DTO.RoleUrlUpdateRequest;
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

    public Page<RoleUrl> getAll(Pageable pageable) {
        return dao.findAll(pageable);
    }

    public RoleUrl getById(String id) {
        return dao.findOne(ignoreDeleted()
                .and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("id"), id))).orElse(null);
    }

    public RoleUrl getByUrl(String url) {
        return dao.findOne(ignoreDeleted()
                .and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("function_url"), url))).orElse(null);
    }

    public RoleUrl getByRoleAndUrl(String role, String url) {
        return dao.findOne(ignoreDeleted()
                .and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("role_id"), role))
                .and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("function_url"), url))).orElse(null);
    }

    public RoleUrl save(RoleUrlCreateRequest request) {
        RoleUrl found = getByRoleAndUrl(request.getRoleId(), request.getUrl());
        if (found != null && found.getDeleted()) {
            found.setDeleted(false);
            dao.save(found);
            return found;
        }
        RoleUrl roleUrl = mapper.fromCreateRequest(request);
        return dao.save(roleUrl);
    }

    public RoleUrl update(String id, RoleUrlUpdateRequest request) {
        RoleUrl found = getById(id);
        RoleUrl updated = mapper.fromUpdateRequest(found, request);
        return dao.save(updated);
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
        return (root, query, criteriaBuilder) -> criteriaBuilder.isFalse(root.get("delete_yn"));
    }

}
