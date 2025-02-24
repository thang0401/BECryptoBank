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
@Table(name = "debit_account")
public class DebitAccount {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "status")
    private String status;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name="balance")
    private Long balance;

    @Column(name = "account_number")
    private String accountNumber;

    @OneToMany(mappedBy = "debitAccount" , cascade = CascadeType.ALL)
    private List<DebitDetail> transactions;

    @OneToMany(mappedBy = "debitAccount",cascade = CascadeType.ALL)
    private List<USDCtoVNDTransaction> USDCtoVNDTransactions;
}
