package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "loan_repayment")
public class LoanRepayment extends BaseEntity {

    @Column(name = "tx_hash", columnDefinition = "TEXT")
    private String txHash;

    @Column(name = "amount")
    private BigDecimal amount = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id")
    private Loan loan;

}