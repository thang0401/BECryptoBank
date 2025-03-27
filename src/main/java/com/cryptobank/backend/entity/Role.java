package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Role role = (Role) o;
        return name.equals(role.name) && Objects.equals(note, role.note);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + Objects.hashCode(note);
        return result;
    }

}