package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(
    name = "role_url",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"role_id", "function_url"})
    }
)
public class RoleUrl extends BaseEntity {

    @Column(name = "function_url", columnDefinition = "TEXT", nullable = false)
    private String functionUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

}