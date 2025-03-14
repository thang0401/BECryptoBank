package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "role_url")
public class RoleUrl extends BaseEntity {

    @Column(name = "function_url", columnDefinition = "TEXT")
    private String functionUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

}