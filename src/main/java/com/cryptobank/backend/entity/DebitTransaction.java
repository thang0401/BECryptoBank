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

    @Column(name = "blockchain_tx_hash", columnDefinition = "TEXT")
    private String blockchainTxHash;

}