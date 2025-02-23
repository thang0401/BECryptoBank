package com.cryptobank.backend.entity;

import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="loan_repayment")
public class LoanRepayment extends BaseEntity {

    @Column(name="tx_hash")
    private String txHash;

    @Column(name="amount")
    private Double amount;

    @Column(name="status")
    private String status;

    @Column(name="loan_id")
    private String loanId;

}
