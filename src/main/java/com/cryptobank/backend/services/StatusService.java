package com.cryptobank.backend.services;

import com.cryptobank.backend.DTO.StatusDTO;
import com.cryptobank.backend.entity.Role;
import com.cryptobank.backend.entity.Status;
import com.cryptobank.backend.exception.ResourceNotFoundException;
import com.cryptobank.backend.mapper.StatusMapper;
import com.cryptobank.backend.repository.StatusDAO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatusService {

    private final StatusDAO dao;
    private final StatusMapper mapper;

    public Status getById(String id) {
        return dao.findOne(ignoreDeleted()
                .and((root, query, cb) -> cb.equal(root.get("id"), id)))
            .orElseThrow(() -> new ResourceNotFoundException("Status with id " + id + " not found or deleted"));
    }

    public StatusDTO toDTO(Status status) {
        return mapper.toDTO(status);
    }

    public Page<StatusDTO> getAll(String groupId, Pageable pageable) {
        Specification<Status> spec = ignoreDeleted();
        if (groupId != null && !groupId.isBlank())
            spec = spec.and((root, query, cb) -> cb.equal(root.get("groupStatus").get("id"), groupId));
        return dao.findAll(spec, pageable).map(this::toDTO);
    }

    private Specification<Status> ignoreDeleted() {
        return (root, query, cb) -> cb.notEqual(root.get("deleted"), true);
    }

}
