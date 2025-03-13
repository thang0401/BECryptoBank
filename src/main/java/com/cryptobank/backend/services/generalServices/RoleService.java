package com.cryptobank.backend.services.generalServices;

import com.cryptobank.backend.DTO.RoleCreateRequest;
import com.cryptobank.backend.DTO.RoleUpdateRequest;
import com.cryptobank.backend.entity.Role;
import com.cryptobank.backend.mapper.RoleMapper;
import com.cryptobank.backend.repository.RoleDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleDAO dao;
    private final RoleMapper roleMapper;

    public Page<Role> getAll(Pageable pageable) {
        return dao.findAll(ignoreDeleted(), pageable);
    }

    public Role getById(String id) {
        return dao.findOne(ignoreDeleted()
                .and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("id"), id))).orElse(null);
    }

    public Role getByName(String roleName) {
        return dao.findOne(ignoreDeleted()
                .and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("name"), roleName))).orElse(null);
    }

    public Role save(RoleCreateRequest request) {
        Role found = getByName(request.getName());
        if (found != null) {
            found.setDeleted(false);
            return dao.save(found);
        }
        Role created = roleMapper.fromCreateRequest(request);
        return dao.save(created);
    }

    public Role update(String id, RoleUpdateRequest request) {
        Role found = getById(id);
        Role updated = roleMapper.fromUpdateRequest(request);
        if (found.equals(updated)) {
            return null;
        }
        updated.setDeleted(false);
        return dao.save(updated);
    }

    public boolean deleteById(String id) {
        Role role = getById(id);
        if (role != null) {
            role.setDeleted(true);
            dao.save(role);
            return true;
        }
        return false;
    }

    private Specification<Role> ignoreDeleted() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isFalse(root.get("delete_yn"));
    }

}
