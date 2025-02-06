package com.cryptobank.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class RolePermissionKey {

    @Column(name = "role_id")
    private String roleId;

    @Column(name = "permission_id")
    private String permissionId;

}
