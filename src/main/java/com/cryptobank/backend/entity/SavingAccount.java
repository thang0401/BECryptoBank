package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@Entity
@Table(name = "saving_account")
public class SavingAccount extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id")
    private Term term;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private Status status;

    @Column(name = "balance")
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "interest_rate")
    private BigDecimal interestRate = BigDecimal.ZERO;

    @Column(name = "maturity_date")
    private OffsetDateTime maturityDate;

    @Column(name = "gg_drive_url", columnDefinition = "TEXT")
    private String ggDriveUrl;

    @Column(name = "heir_status")
    private Boolean heirStatus = false;

    @Column(name = "heir_name", columnDefinition = "TEXT")
    private String heirName;

    @JsonIgnore
    @OneToMany(mappedBy = "savingAccount")
    private List<SavingTransaction> savingTransactions = new ArrayList<>();

}