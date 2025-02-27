package com.cryptobank.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="referral_bonus")
public class ReferralBonus extends BaseEntity {

    @JsonIgnore
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
