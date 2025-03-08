package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "sub_wallet")
public class SubWallet extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "wallet_address")
    private String walletAddress;

    @Column(name = "private_key")
    private String privateKey;

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
    private OffsetDateTime verifiedAt;

    @Column(name = "first_verified_at")
    private OffsetDateTime firstVerifiedAt;

    @Column(name = "latest_verified_at")
    private OffsetDateTime latestVerifiedAt;

    @Column(name = "custom_metadata")
    private String customMetadata;

    @Column(name = "balance")
    private BigDecimal balance = BigDecimal.ZERO;

}