package com.cryptobank.backend.entity;

import java.time.ZonedDateTime;
import java.util.List;

import org.hibernate.annotations.ManyToAny;

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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="saving_account")
public class SavingAccount extends BaseEntity {

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
    
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

    @OneToMany(mappedBy = "savingAccount",cascade = CascadeType.ALL)
    private List<Heir> heirs;

    @OneToMany(mappedBy = "savingAccount",cascade = CascadeType.ALL)
    private List<SavingTransaction> transactions;

}
