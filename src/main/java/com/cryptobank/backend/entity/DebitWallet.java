package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "debit_account")
public class DebitWallet extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "balance")
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "wallet_address", columnDefinition = "TEXT")
    private String walletAddress;

    @Column(name = "private_key", columnDefinition = "TEXT")
    private String privateKey;

    @Column(name = "chain_type", columnDefinition = "TEXT")
    private String chainType;

    @Column(name = "wallet_client_type", columnDefinition = "TEXT")
    private String walletClientType;

    @Column(name = "connector_type", columnDefinition = "TEXT")
    private String connectorType;

    @Column(name = "recovery_method", columnDefinition = "TEXT")
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

    @Column(name = "custom_metadata", columnDefinition = "TEXT")
    private String customMetadata;

    @OneToMany(mappedBy = "debitWallet")
    private List<DebitTransaction> debitTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "debitWallet")
    private List<UsdcVndTransaction> usdcVndTransactions = new ArrayList<>();

}