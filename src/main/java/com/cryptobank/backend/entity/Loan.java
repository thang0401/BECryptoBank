package com.cryptobank.backend.entity;

import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name="loan")
@AllArgsConstructor
@NoArgsConstructor
public class Loan {
    @Id
    private String id;

    @Column(name="user_id")
    private String userId;

    @Column(name="loan_amount")
    private Double loanAmount;

    @Column(name="interest_rate")
    private Double interestRate;

    @Column(name="due_date")
    private ZonedDateTime dueDate;

    @Column(name="loan_status")
    private String loanStatus;

    @Column(name="term_id")
    private String termId;

    @Column(name="create_at")
    private ZonedDateTime createAt;
}
