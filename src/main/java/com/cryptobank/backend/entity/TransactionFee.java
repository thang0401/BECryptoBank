package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "transaction_fee")
public class TransactionFee extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "internal_transaction_id")
    private InternalTransaction internalTransaction;

    @Column(name = "discount_rate")
    private Double discountRate;

    @Column(name = "fee_amount")
    private Double feeAmount;

}