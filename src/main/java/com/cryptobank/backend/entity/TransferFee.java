package com.cryptobank.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transaction_fee")
public class TransferFee extends BaseEntity {

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="debit_transaction_id")
    private DebitDetail debitTransaction;

    @Column(name = "discount_rate")
    private Double discount;

    @Column(name = "fee_amount")
    private Double amount;

}
