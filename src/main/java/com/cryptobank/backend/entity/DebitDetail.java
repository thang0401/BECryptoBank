package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "debit_transaction")
public class DebitDetail {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

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

    @ManyToOne
    @JoinColumn(name = "debit_id")
    private DebitAccount debitAccount;

    @ManyToOne
    @JoinColumn(name="receiver_id")
    private User receiver;

    @Column(name="transaction_type")
    private String transactionType;

    @Column(name="transaction_hash")
    private String transactionHash;

    @Column(name = "from_pubKey")
    private String fromPubkey;

    @Column(name = "to_pubKey")
    private String toPubkey;

    @OneToMany(mappedBy = "debitTransaction",cascade = CascadeType.ALL)
    private List<TransferFee> transferFees;
}
