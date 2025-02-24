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

@Entity
@Data
@Table(name="referral_bonus")
@NoArgsConstructor
@AllArgsConstructor
public class ReferralBonus {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @Column(name="bonus_amount")
    private Double bonusAmount;

    @Column(name="status_id")
    private String statusId;

    @Column(name="referral_user_id")
    private String referralUserId;

    @Column(name="create_at")
    private ZonedDateTime createAt;
}
