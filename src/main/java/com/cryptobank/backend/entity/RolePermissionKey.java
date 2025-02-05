package com.cryptobank.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class RolePermissionKey {

    @Column(name = "role_id")
    private String role_id;

    @Column(name = "permission_id")
    private String permission_id;

}
