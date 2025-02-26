package com.cryptobank.backend.entity;

import java.time.ZonedDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="loan")
public class Loan extends BaseEntity {

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @Column(name="loan_amount")
    private Double loanAmount;

    @Column(name="interest_rate")
    private Double interestRate;

    @Column(name="due_day")
    private ZonedDateTime dueDate;

    @Column(name="loan_status")
    private String loanStatus;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="term_id")
    private Term term;

    @OneToMany(mappedBy = "loan",cascade = CascadeType.ALL)
    private List<LoanRepayment> repayments;
}
