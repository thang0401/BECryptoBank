package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "saving_transaction")
public class SavingTransaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saving_account_id")
    private SavingAccount savingAccount;

    @Column(name = "amount")
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(name = "transaction_type", columnDefinition = "TEXT")
    private String transactionType;

}