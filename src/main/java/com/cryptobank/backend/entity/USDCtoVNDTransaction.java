package com.cryptobank.backend.entity;

import java.time.ZonedDateTime;

import org.hibernate.annotations.ManyToAny;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="usdc_vnd_transaction")
public class USDCtoVNDTransaction extends BaseEntity {

    @ManyToOne
    @JoinColumn(name="debit_account_id")
    private DebitAccount debitAccount;

    @Column(name="VND_amount")
    private Double vndAmount;

    @Column(name="USDC_amount")
    private Double usdcAmount;

    @Column(name="exchange_rate")
    private Double exchangeRate;

    @Column(name="status")
    private String status;

}
