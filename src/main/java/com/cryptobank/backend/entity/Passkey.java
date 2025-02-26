package com.cryptobank.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "passkeys")
public class Passkey {

    @Id
    private String id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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
    private ZonedDateTime verifiedAt;

    @Column(name = "first_verified_at")
    private ZonedDateTime firstVerifiedAt;

    @Column(name = "latest_verified_at")
    private ZonedDateTime latestVerifiedAt;

}
