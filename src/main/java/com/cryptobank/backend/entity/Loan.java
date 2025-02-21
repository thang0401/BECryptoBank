package com.cryptobank.backend.entity;

import java.time.ZonedDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @Column(name="loan_amount")
    private Double loanAmount;

    @Column(name="interest_rate")
    private Double interestRate;

    @Column(name="due_date")
    private ZonedDateTime dueDate;

    @Column(name="loan_status")
    private String loanStatus;

    @ManyToOne
    @JoinColumn(name="term_id")
    private Term term;


    @Column(name="create_at")
    private ZonedDateTime createAt;

    @OneToMany(mappedBy = "loan",cascade = CascadeType.ALL)
    private List<LoanRepayment> repayments;
}
