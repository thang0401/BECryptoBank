package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import org.hibernate.annotations.GenericGenerator;

@Getter
@Setter
@Entity
@Table(name = "usdc_vnd_transaction")
public class UsdcVndTransaction {

    @Id
    @Column(name = "id", nullable = false, length = 255)
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debit_account_id", referencedColumnName = "id")
    private DebitWallet debitWallet;

    @Column(name = "vnd_amount", nullable = false)
    private BigDecimal vndAmount;

    @Column(name = "usdc_amount", nullable = false)
    private BigDecimal usdcAmount;

    @Column(name = "exchange_rate", nullable = false)
    private BigDecimal exchangeRate;

    @Column(name = "type", columnDefinition = "TEXT", nullable = false)
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    private Status status;

    @Column(name = "delete_yn", nullable = false)
    private Boolean deleteYn = false;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE", updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "modified_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime modifiedAt;

    @Column(name = "created_by", columnDefinition = "TEXT")
    private String createdBy;

    @Column(name = "modified_by", columnDefinition = "TEXT")
    private String modifiedBy;
    
    @Column(name = "ma_giao_dich_banking", columnDefinition = "TEXT")
    private String maGiaoDichBanking;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.modifiedAt = OffsetDateTime.now();
    }
}
