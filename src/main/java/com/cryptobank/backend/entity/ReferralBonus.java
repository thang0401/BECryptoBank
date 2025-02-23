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
@Table(name="referral_bonus")
public class ReferralBonus extends BaseEntity {

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @Column(name="bonus_amount")
    private Double bonusAmount;

    @Column(name="status_id")
    private String statusId;

    @Column(name="referral_user_id")
    private String referralUserId;

}
