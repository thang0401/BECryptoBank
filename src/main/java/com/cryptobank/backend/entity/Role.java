package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "role")
public class Role extends BaseEntity {

    @Column(name = "name", columnDefinition = "TEXT")
    private String name;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private Status status;

    @OneToMany(mappedBy = "role")
    private List<RoleUrl> roleUrls = new ArrayList<>();

    @OneToMany(mappedBy = "role")
    private List<UserRole> userRoles = new ArrayList<>();

}