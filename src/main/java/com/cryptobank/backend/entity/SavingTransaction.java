package com.cryptobank.backend.entity;

import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
    
    @Column(name="user_id")
    private String userId;

    @Column(name="saving_account")
    private String savingAccount;

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
