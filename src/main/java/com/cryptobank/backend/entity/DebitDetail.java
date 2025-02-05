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
@Table(name = "debit_detail")
public class DebitDetail {

    @Id
    private String id;
    private double amount;

    @ManyToOne
    @JoinColumn(name = "asset_type_id")
    private AssetType assetType;

    @Column(name = "delete_yn")
    private boolean deleted;

    @Column(name = "created_at")
    private LocalDateTime createdDate;

    @Column(name = "created_by")
    private String createdBy;

    @ManyToOne
    @JoinColumn(name = "debit_id")
    private Debit debit;

}
