package com.cryptobank.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "term")
public class Term extends BaseEntity {

    @Column(name = "amount_month")
    private Long amountMonth;

    @Column(name = "type", columnDefinition = "TEXT")
    private String type;

    @Column(name = "interest_rate_of_month")
    private BigDecimal interestRateOfMonth = BigDecimal.ZERO;

}