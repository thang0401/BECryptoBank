package com.cryptobank.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString(callSuper = true)
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

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="loan_id")
    private Loan loan;

}
