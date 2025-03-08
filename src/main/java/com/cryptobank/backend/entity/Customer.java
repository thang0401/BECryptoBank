package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "customer")
public class Customer extends BaseEntity {

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "gender")
    private String gender;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "smart_otp")
    private String smartOtp;

    @Column(name = "date_of_birth")
    private OffsetDateTime dateOfBirth;

    @Column(name = "id_card_number")
    private String idCardNumber;

    @Column(name = "id_card_front_img_url")
    private String idCardFrontImgUrl;

    @Column(name = "id_card_back_img_url")
    private String idCardBackImgUrl;

    @Column(name = "kyc_status")
    private Boolean kycStatus = false;

    @Column(name = "home_address")
    private String homeAddress;

    @Column(name = "ward")
    private String ward;

    @Column(name = "district")
    private String district;

    @Column(name = "province")
    private String province;

    @Column(name = "nation")
    private String nation;

    @Column(name = "privy_id")
    private String privyId;

    @Column(name = "wallet_address")
    private String walletAddress;

    @Column(name = "has_accepted_terms")
    private Boolean hasAcceptedTerms = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ranking_id")
    private Ranking ranking;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

}