package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "role_permission")
public class RolePermission {

    @EmbeddedId
    RolePermissionKey id;

    @ManyToOne
    @MapsId("role_id")
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne
    @MapsId("permission_id")
    @JoinColumn(name = "permission_id")
    private Permission permission;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

}
