package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "debit_account")
public class DebitAccount {

    @Id
    private String id;

    @JsonIgnore
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
    private Double balance;
    
    @JsonIgnore
    @OneToMany(mappedBy = "debitAccount" , cascade = CascadeType.ALL)
    private List<DebitDetail> transactions;

    @JsonIgnore
    @OneToMany(mappedBy = "debitAccount",cascade = CascadeType.ALL)
    private List<USDCtoVNDTransaction> USDCtoVNDTransactions;
}
