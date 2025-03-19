package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "status")
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "status")
    private List<DebitTransaction> debitTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "status", orphanRemoval = true)
    private List<GroupStatus> groupStatuses = new ArrayList<>();

    @OneToMany(mappedBy = "status")
    private List<Loan> loans = new ArrayList<>();

    @OneToMany(mappedBy = "status")
    private List<LoanRepayment> loanRepayments = new ArrayList<>();

    @OneToMany(mappedBy = "status")
    private List<Role> roles = new ArrayList<>();

    @OneToMany(mappedBy = "status")
    private List<SuspiciousActivity> suspiciousActivities = new ArrayList<>();

    @OneToMany(mappedBy = "status")
    private List<UsdcVndTransaction> usdcVndTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "status")
    private List<ReferralBonus> referralBonuses = new ArrayList<>();

    @OneToMany(mappedBy = "status")
    private List<SavingAccount> savingAccounts = new ArrayList<>();

}