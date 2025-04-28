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

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleDAO dao;
    private final RoleMapper mapper;
    private final StatusService statusService;

    public Page<RoleDTO> getAll(String statusId, Pageable pageable) {
        Specification<Role> spec = ignoreDeleted();
        if (statusId != null && !statusId.isBlank())
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status").get("id"), statusId));
        return dao.findAll(spec, pageable).map(mapper::toDTO);
    }

    public RoleDTO toDTOFromId(String id) {
        Role role = getById(id);
        return mapper.toDTO(role);
    }

    public RoleDTO toResponseFromName(String name) {
        Role role = getByName(name);
        return mapper.toDTO(role);
    }

    public Role getById(String id) {
        return dao.findOne(ignoreDeleted()
                .and((root, query, cb) -> cb.equal(root.get("id"), id)))
            .orElseThrow(() -> new ResourceNotFoundException("Role with id " + id + " not found"));
    }

    public Role getByName(String roleName) {
        return dao.findOne(ignoreDeleted()
                .and((root, query, cb) -> cb.equal(root.get("name"), roleName)))
            .orElseThrow(() -> new ResourceNotFoundException("Role with name " + roleName + " not found"));
    }

    public RoleDTO save(RoleCreateRequest request) {
        boolean found = dao.exists(ignoreDeleted()
                .and((root, query, cb) -> cb.equal(root.get("name"), request.getName())));
        if (found) {
            throw new AlreadyExistException("Role with name " + request.getName() + " already exist");
        }
        Role created = mapper.fromCreateRequest(request);
        if (request.getStatusId() != null && !request.getStatusId().isBlank()) {
            created.setStatus(statusService.getById(request.getStatusId()));
        } else {
            created.setStatus(statusService.getById("cvvvgqbme6nnaun2s4ng"));
        }
        return mapper.toDTO(dao.save(created));
    }

    public RoleDTO update(String id, RoleUpdateRequest request) {
        Role found = getById(id);
        if (request.isSimilar(found)) {
            return mapper.toDTO(found);
        }
        Role updated = mapper.fromUpdateRequest(found, request);
        if (request.getStatusId() != null && !request.getStatusId().isBlank()) {
            updated.setStatus(statusService.getById(request.getStatusId()));
        }
        updated.setModifiedAt(OffsetDateTime.now());
        return mapper.toDTO(dao.save(updated));
    }

    public boolean deleteById(String id) {
        Role role = getById(id);
        if (role.getDeleted()) {
            return false;
        }
        role.setDeleted(true);
        role.setModifiedAt(OffsetDateTime.now());
        dao.save(role);
        return true;
    }

    private Specification<Role> ignoreDeleted() {
        return (root, query, cb) -> cb.notEqual(root.get("deleted"), true);
    }

}
