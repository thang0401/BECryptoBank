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
@Table(name = "sub_wallet")
public class SubWallet {

    @Id
    private String id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "wallet_address")
    private String address;

    @Column(name = "chain_type")
    private String chainType;

    @Column(name = "wallet_client_type")
    private String walletClientType;

    @Column(name = "connector_type")
    private String connectorType;

    @Column(name = "recovery_method")
    private String recoveryMethod;

    @Column(name = "imported")
    private Boolean imported;

    @Column(name = "delegated")
    private Boolean delegated;

    @Column(name = "wallet_index")
    private Integer walletIndex;

    @Column(name = "verified_at")
    private ZonedDateTime verifiedAt;

    @Column(name = "first_verified_at")
    private ZonedDateTime firstVerifiedAt;

    @Column(name = "latest_verified_at")
    private ZonedDateTime latestVerifiedAt;

}
