package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "withdraw_portfolio_history")
public class WithdrawPortfolioHistory {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "received_pubkey")
    private String receivedPublicKey;

    @ManyToOne
    @JoinColumn(name = "from_portfolio_id")
    private Portfolio fromPortfolio;

    @Column(name = "delete_yn")
    private boolean delete;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "created_by_customer")
    private String createdBy_customer;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @Column(name = "modified_by")
    private String modifiedBy;
}
