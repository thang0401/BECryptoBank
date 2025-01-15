package com.cryptobank.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transfer_fee")
public class TransferFee {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "transfer_history_id")
    private String transferHistoryId;

    @Column(name = "discount_rate")
    private Double discount;

    @Column(name = "fee_amount")
    private Double amount;

    @Column(name = "delete_yn")
    private boolean delete;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @Column(name = "modified_by")
    private String modifiedBy;
}
