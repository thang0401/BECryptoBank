package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "debit_transaction")
public class DebitDetail extends BaseEntity {

    @ManyToOne
    @JoinColumn(name="sender_id")
    private User sender;

    @Column(name="amount")
    private Double amount;

    @Column(name="status")
    private String status;

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

    @OneToMany(mappedBy = "debitTransaction", cascade = CascadeType.ALL)
    private List<TransferFee> transferFees;
}
