package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "usdc_vnd_transaction")
public class UsdcVndTransaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debit_account_id")
    private DebitWallet debitWallet;

    @Column(name = "VND_amount")
    private BigDecimal vndAmount;

    @Column(name = "USDC_amount")
    private BigDecimal usdcAmount;

    @Column(name = "exchange_rate")
    private BigDecimal exchangeRate;

    @Column(name = "type", columnDefinition = "TEXT")
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private Status status;

}