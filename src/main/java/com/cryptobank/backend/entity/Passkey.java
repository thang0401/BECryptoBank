package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.time.OffsetTime;

@Getter
@Setter
@Entity
@Table(name = "passkeys")
public class Passkey {
    @Id
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "credential_id", columnDefinition = "TEXT")
    private String credentialId;

    @Column(name = "authenticator_name", columnDefinition = "TEXT")
    private String authenticatorName;

    @Column(name = "created_with_os", columnDefinition = "TEXT")
    private String createdWithOs;

    @Column(name = "created_with_browser", columnDefinition = "TEXT")
    private String createdWithBrowser;

    @Column(name = "enrolled_in_mfa")
    private Boolean enrolledInMfa;

    @Column(name = "verified_at")
    private OffsetDateTime verifiedAt;

    @Column(name = "first_verified_at")
    private OffsetDateTime firstVerifiedAt;

    @Column(name = "latest_verified_at")
    private OffsetTime latestVerifiedAt;

}