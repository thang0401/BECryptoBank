package com.cryptobank.backend.services;

import com.cryptobank.backend.DTO.RoleDTO;
import com.cryptobank.backend.DTO.request.RoleCreateRequest;
import com.cryptobank.backend.DTO.request.RoleUpdateRequest;
import com.cryptobank.backend.entity.Role;
import com.cryptobank.backend.exception.AlreadyExistException;
import com.cryptobank.backend.exception.ResourceNotFoundException;
import com.cryptobank.backend.mapper.RoleMapper;
import com.cryptobank.backend.repository.RoleDAO;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RoleService {

    private final RoleDAO dao;
    private final RoleMapper mapper;

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
                .and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("id"), id)))
            .orElseThrow(() -> new ResourceNotFoundException("Role with id " + id + " not found"));
    }

    public Role getByName(String roleName) {
        return dao.findOne(ignoreDeleted()
                .and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("name"), roleName)))
            .orElseThrow(() -> new ResourceNotFoundException("Role with name " + roleName + " not found"));
    }

    public RoleDTO save(RoleCreateRequest request) {
        boolean found = dao.exists(ignoreDeleted()
                .and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("name"), request.getName())));
        if (found) {
            throw new AlreadyExistException("Role with name " + request.getName() + " already exist");
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
            role.setModifiedAt(OffsetDateTime.now());
            dao.save(role);
            return true;
        }
        return false;
    }

    private Specification<Role> ignoreDeleted() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get("deleted"), true);
    }

}
