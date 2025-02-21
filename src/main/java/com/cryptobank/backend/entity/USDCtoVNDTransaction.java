package com.cryptobank.backend.entity;

import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name="usdc_vnd_transaction")
@AllArgsConstructor
@NoArgsConstructor
public class USDCtoVNDTransaction {
    @Id
    private String id;

    @Column(name="debit_account_id")
    private String debitAccountId;

    @Column(name="VND_amount")
    private Double VNDamount;

    @Column(name="USDC_amount")
    private Double USDCamount;

    @Column(name="exchange_rate")
    private Double exchangeRate;

    @Column(name="status")
    private String status;

    @Column(name="create_at")
    private ZonedDateTime createAt;
}
