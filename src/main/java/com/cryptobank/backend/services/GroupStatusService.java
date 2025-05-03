package com.cryptobank.backend.services;

import com.cryptobank.backend.DTO.GroupStatusDTO;
import com.cryptobank.backend.entity.GroupStatus;
import com.cryptobank.backend.mapper.GroupStatusMapper;
import com.cryptobank.backend.repository.GroupStatusDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupStatusService {

    private final GroupStatusDAO dao;
    private final GroupStatusMapper mapper;

    public Page<GroupStatusDTO> getAll(String name, Pageable pageable) {
        Specification<GroupStatus> spec = ignoreDeleted();
        if (name != null && !name.isBlank())
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        return dao.findAll(spec, pageable).map(mapper::toDTO);
    }

    private Specification<GroupStatus> ignoreDeleted() {
        return (root, query, cb) -> cb.notEqual(root.get("deleted"), true);
    }

}
