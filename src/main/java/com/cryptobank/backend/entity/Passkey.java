package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "passkeys")
public class Passkey {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Customer customer;

    @Column(name = "credential_id")
    private String credentialId;

    @Column(name = "authenticator_name")
    private String authenticatorName;

    @Column(name = "created_with_os")
    private String createdWithOs;

    @Column(name = "created_with_browser")
    private String createdWithBrowser;

    @Column(name = "enrolled_in_mfa")
    private Boolean enrolledInMfa;

    @Column(name = "verified_at")
    private OffsetDateTime verifiedAt;

    @Column(name = "first_verified_at")
    private OffsetDateTime firstVerifiedAt;

    @Column(name = "latest_verified_at")
    private OffsetTime latestVerifiedAt;

    @ColumnDefault("gen_random_uuid()")
    @Column(name = "uuid_id")
    private UUID uuidId;

}