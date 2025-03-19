package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "suspicious_activity")
public class SuspiciousActivity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debit_transaction_id")
    private DebitTransaction debitTransaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private Status status;

    @Column(name = "rule_name", columnDefinition = "TEXT")
    private String ruleName;

    @Column(name = "threshold_value")
    private BigDecimal thresholdValue = BigDecimal.ZERO;

    @Column(name = "time_window")
    private Integer timeWindow;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "detected_at")
    private OffsetDateTime detectedAt;

}