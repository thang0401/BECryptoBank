package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "debit_transaction")
public class DebitDetail {

    @Id
    private String id;

    @Column(name="sender_id")
    private String sender;

    @Column(name="amount")
    private Double amount;

    @Column(name="status")
    private String status;

    @Column(name = "created_at")
    private ZonedDateTime createdDate;

    @Column(name = "created_by")
    private String createdBy;

    
    @Column(name = "delete_yn")
    private boolean deleted;

    @Column(name = "debit_id")
    private String debit;

    @Column(name="receiver_id")
    private String receiver;

    @Column(name="transaction_type")
    private String transactionType;

    @Column(name="transaction_hash")
    private String transactionHash;
}
