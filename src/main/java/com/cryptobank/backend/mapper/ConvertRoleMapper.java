package com.cryptobank.backend.mapper;

import com.cryptobank.backend.entity.Role;
import com.cryptobank.backend.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ConvertRoleMapper {

    private final RoleService roleService;

    public Role toRole(String id) {
        return id == null ? null : roleService.getById(id);
    }

    public String toString(Role role) {
        return role == null ? null : role.getName();
    }

}
