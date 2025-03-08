package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity này được sử dụng trong chức năng Authentication.<br>
 * Sẽ được tạo thành JWT.
 */
@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    private String username;

    private String email;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    private String providerId;

    private String fullName;

    private String avatar;

    private LocalDateTime lastLoginAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private Status status;

    @OneToOne(mappedBy = "user")
    private Customer customer;

    private enum Provider {
        GOOGLE, GITHUB
    }

}
