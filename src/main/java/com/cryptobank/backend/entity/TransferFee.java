package com.cryptobank.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transfer_fee")
public class TransferFee extends BaseEntity {

    @ManyToOne
    @JoinColumn(name="debit_transaction_id")
    private DebitDetail debitTransaction;

    @Column(name = "discount_rate")
    private Double discount;

    @Column(name = "fee_amount")
    private Double amount;

}
