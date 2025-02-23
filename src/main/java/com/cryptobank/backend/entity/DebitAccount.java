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
@Table(name = "debit_account")
public class DebitAccount extends BaseEntity {

    @Column(name = "debit_number")
    private String debitNumber;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "status")
    private String status;

    @Column(name="balance")
    private Long balance;

    @Column(name = "account_number")
    private String accountNumber;

    @OneToMany(mappedBy = "debitAccount" , cascade = CascadeType.ALL)
    private List<DebitDetail> transactions;

    @OneToMany(mappedBy = "debitAccount",cascade = CascadeType.ALL)
    private List<USDCtoVNDTransaction> USDCtoVNDTransactions;
}
