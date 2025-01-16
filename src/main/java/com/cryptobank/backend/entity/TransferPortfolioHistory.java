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
@Table(name = "transfer_portfolio_history")
public class TransferPortfolioHistory {

    @Id
    @Column(name = "id")
    private String id;

    @ManyToOne
    @JoinColumn(name = "received_portfolio_id")
    private Portfolio receivedPortfolio;

    @ManyToOne
    @JoinColumn(name = "send_portfolio_id")
    private Portfolio sentPortfolio;

    @Column(name = "note")
    private String note;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private Status status;

    @Column(name = "delete_yn")
    private boolean deleted;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @Column(name = "modified_by")
    private String modifiedBy;
}	
