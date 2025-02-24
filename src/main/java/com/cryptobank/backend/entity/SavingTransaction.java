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

@Entity
@Data
@Table(name="saving_transaction")
@NoArgsConstructor
@AllArgsConstructor
public class SavingTransaction {
    @Id
    private String id;
    
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name="saving_account")
    private SavingAccount savingAccount;

    @Column(name="delete_yn")
    private Boolean isDeleted;

    @Column(name="created_date")
    private ZonedDateTime createdDate;

    @Column(name="created_by")
    private String createdBy;

    @Column(name = "modified_date")
    private ZonedDateTime modifiedDate;

    @Column(name = "modified_by")
    private String modifiedBy;

    @Column(name="amount")
    private Double amount;

    @Column(name="transaction_type")
    private String transactionType;
}
