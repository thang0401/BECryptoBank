package com.cryptobank.backend.entity;

import java.security.Timestamp;
import java.time.ZonedDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="saving_account")
public class SavingAccount extends BaseEntity {

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="term_id")
    private Term term;

    @Column(name="note")
    private String note;

    @Column(name="status_id")
    private String statusId;

    @Column(name="balance")
    private Double balance;

    @Column(name="interest_rate")
    private Double interestRate;

    @Column(name="maturity_date")
    private ZonedDateTime maturityDate;

    @Version
    @Column(name = "modified_date")
    private Timestamp modifiedDate;

    @JsonIgnore
    @OneToMany(mappedBy = "savingAccount",cascade = CascadeType.ALL)
    private List<SavingTransaction> transactions;

}
