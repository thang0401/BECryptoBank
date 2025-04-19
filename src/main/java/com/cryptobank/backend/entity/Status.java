package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@Entity
@Table(name = "status")
public class Status extends BaseEntity {

    @Column(name = "name", columnDefinition = "TEXT")
    private String name;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_status_id")
    private GroupStatus groupStatus;

    @JsonIgnore
    @OneToMany(mappedBy = "status",fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<>();
    @JsonIgnore

    @OneToMany(mappedBy = "status",fetch = FetchType.LAZY)
    private List<DebitTransaction> debitTransactions = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "status",fetch = FetchType.LAZY)
    private List<Loan> loans = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "status",fetch = FetchType.LAZY)
    private List<LoanRepayment> loanRepayments = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "status",fetch = FetchType.LAZY)
    private List<Role> roles = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "status",fetch = FetchType.LAZY)
    private List<SuspiciousActivity> suspiciousActivities = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "status",fetch = FetchType.LAZY)
    private List<UsdcVndTransaction> usdcVndTransactions = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "status",fetch = FetchType.LAZY)
    private List<ReferralBonus> referralBonuses = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "status",fetch = FetchType.LAZY)
    private List<SavingAccount> savingAccounts = new ArrayList<>();

}