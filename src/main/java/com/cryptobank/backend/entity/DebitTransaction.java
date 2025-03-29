package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@Entity
@Table(name = "debit_transaction")
public class DebitTransaction extends BaseEntity {

    @Column(name = "amount")
    private BigDecimal amount = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debit_wallet_id")
    private DebitWallet debitWallet;

    @Column(name = "transaction_type", columnDefinition = "TEXT")
    private String transactionType;

    @Column(name = "transaction_hash", columnDefinition = "TEXT")
    private String transactionHash;

    @Column(name = "from_pub_Key", columnDefinition = "TEXT")
    private String fromPubKey;

    @Column(name = "to_pub_Key", columnDefinition = "TEXT")
    private String toPubKey;
 
    @JsonIgnore
    @OneToMany(mappedBy = "debitTransaction")
    private List<SuspiciousActivity> suspiciousActivities = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "debitTransaction")
    private List<TransactionFee> transactionFees = new ArrayList<>();

}