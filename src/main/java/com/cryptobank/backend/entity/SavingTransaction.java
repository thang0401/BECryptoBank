package com.cryptobank.backend.entity;

import java.time.ZonedDateTime;

import org.hibernate.annotations.ManyToAny;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="saving_transaction")
public class SavingTransaction extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name="saving_account")
    private SavingAccount savingAccount;

    @Column(name="amount")
    private Double amount;

    @Column(name="transaction_type")
    private String transactionType;
}
