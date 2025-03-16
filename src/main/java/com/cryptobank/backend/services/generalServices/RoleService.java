package com.cryptobank.backend.services.generalServices;

import com.cryptobank.backend.DTO.RoleDTO;
import com.cryptobank.backend.DTO.request.RoleCreateRequest;
import com.cryptobank.backend.DTO.request.RoleUpdateRequest;
import com.cryptobank.backend.entity.Role;
import com.cryptobank.backend.entity.UserRole;
import com.cryptobank.backend.mapper.RoleMapper;
import com.cryptobank.backend.repository.RoleDAO;
import com.cryptobank.backend.repository.UserRoleDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RoleService {

    private final RoleDAO dao;
    private final UserRoleDAO userRoleDAO;
    private final RoleMapper mapper;

    public int count(String id) {
        List<UserRole> userRoles = userRoleDAO.findAll(
                (root, query, criteriaBuilder) -> criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("role").get("id"), id),
                        criteriaBuilder.notEqual(root.get("deleted"), true)
                ));
        return userRoles.size();
    }

    public Page<RoleDTO> getAll(Pageable pageable) {
        Page<Role> roles = dao.findAll(ignoreDeleted(), pageable);
        return roles.map(mapper::toResponse);
    }

    public RoleDTO toResponseFromId(String id) {
        Role role = getById(id);
        return role == null ? null : mapper.toResponse(role);
    }

    public RoleDTO toResponseFromName(String name) {
        Role role = getByName(name);
        return role == null ? null : mapper.toResponse(role);
    }

    public Role getById(String id) {
        return dao.findOne(ignoreDeleted()
                .and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("id"), id))).orElse(null);
    }

    public Role getByName(String roleName) {
        return dao.findOne(ignoreDeleted()
                .and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("name"), roleName))).orElse(null);
    }

    public RoleDTO save(RoleCreateRequest request) {
        Role found = getByName(request.getName());
        if (found != null) {
            found.setDeleted(false);
            return mapper.toResponse(dao.save(found));
        }
        Role created = mapper.fromCreateRequest(request);
        return mapper.toResponse(dao.save(created));
    }

    public RoleDTO update(String id, RoleUpdateRequest request) {
        Role found = getById(id);
        Role updated = mapper.fromUpdateRequest(found, request);
        return mapper.toResponse(dao.save(updated));
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
        return (root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get("deleted"), true);
    }

}
