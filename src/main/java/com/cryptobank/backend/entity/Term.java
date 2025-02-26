package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "term")
public class Term extends BaseEntity {

    @Column(name = "amount_month")
    private Long amount_month;

    @Column(name = "type")
    private String type;

    @Column(name = "interest_rate_of_month")
    private Double interestRateOfMonth;

}
