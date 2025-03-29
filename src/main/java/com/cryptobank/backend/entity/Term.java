package com.cryptobank.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@Entity
@Table(name = "term")
public class Term extends BaseEntity {

    @Column(name = "amount_month")
    private Long amountMonth;

    @Column(name = "type", columnDefinition = "TEXT")
    private String type;

    @Column(name = "interest_rate_of_month")
    private BigDecimal interestRateOfMonth = BigDecimal.ZERO;

    @JsonIgnore
    @OneToMany(mappedBy = "term")
    private List<Loan> loans = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "term")
    private List<SavingAccount> savingAccounts = new ArrayList<>();

}