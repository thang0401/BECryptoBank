package com.cryptobank.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "debit_transaction")
public class DebitDetail extends BaseEntity {

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="user_id")
    private User sender;

    @Column(name="amount")
    private Double amount;

    @Column(name="status")
    private String status;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "debit_id")
    private DebitAccount debitAccount;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="receiver_id")
    private User receiver;

    @Column(name="transaction_type")
    private String transactionType;

    @Column(name="transaction_hash")
    private String transactionHash;

}
