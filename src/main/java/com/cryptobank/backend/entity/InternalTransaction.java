package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "internal_transaction")
@Data
@NoArgsConstructor
public class InternalTransaction extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "from_debit_account_id")
    private DebitWallet fromAccount;

    @ManyToOne
    @JoinColumn(name = "to_debit_account_id")
    private DebitWallet toAccount;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "type")
    private String type; // TRANSFER hoặc RECEIVE

    @Column(name = "note")
    private String note;


    @ManyToOne
    @JoinColumn(name = "status_id")
    private Status status;

    @OneToMany(mappedBy = "internalTransaction", cascade = CascadeType.ALL)
    private List<TransactionFee> transactionFees = new ArrayList<>();

}