package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "saving_account")
public class SavingAccount extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id")
    private Term term;

    @Column(name = "note")
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

    @Column(name = "gg_drive_url")
    private String ggDriveUrl;

    @Column(name = "heir_status")
    private Boolean heirStatus = false;

    @Column(name = "heir_name")
    private String heirName;

}