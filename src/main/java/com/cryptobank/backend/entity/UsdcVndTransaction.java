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
    @JoinColumn(name = "debit_wallet_id")
    private DebitWallet debitWallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private Status status;

    @Column(name = "VND_amount")
    private BigDecimal vndAmount;

    @Column(name = "USDC_amount")
    private BigDecimal usdcAmount;

    @Column(name = "exchange_rate")
    private BigDecimal exchangeRate;

    @Column(name = "type", columnDefinition = "TEXT")
    private String type;

}