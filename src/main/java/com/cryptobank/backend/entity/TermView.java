package com.cryptobank.backend.entity;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;


@Entity
@Table(name="term_deleted_false")
@Immutable
@Getter
public class TermView extends BaseEntity {

    @Id
    private String id;

    @Column(name="amount_month")
    private Long amountMonth;

    @Column(name="type")
    private String type;
    
    @Column(name="interest_rate_of_month")
    private Double interestRate;
}
