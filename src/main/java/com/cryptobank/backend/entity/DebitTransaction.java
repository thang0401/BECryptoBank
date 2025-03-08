package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

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
    @JoinColumn(name = "debit_id")
    private DebitAccount debit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private Customer receiver;

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "transaction_hash")
    private String transactionHash;

    @Column(name = "from_pubKey")
    private String fromPubKey;

    @Column(name = "to_pubKey")
    private String toPubKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_wallet_id")
    private SubWallet subWallet;

}