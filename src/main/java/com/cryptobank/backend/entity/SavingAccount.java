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
@Table(name="saving_account")
@AllArgsConstructor
@NoArgsConstructor
public class SavingAccount {
    @Id
    private String id;

    @Column(name="user_id")
    private String userId;
    
    @Column(name="term_id")
    private String termId;

    @Column(name="note")
    private String note;

    @Column(name="status_id")
    private String statusId;

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

    @Column(name="balance")
    private Double balance;

    @Column(name="interest_rate")
    private Double interestRate;

    @Column(name="maturity_date")
    private ZonedDateTime maturityDate;
}
