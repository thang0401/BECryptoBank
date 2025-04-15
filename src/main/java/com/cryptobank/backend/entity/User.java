package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Entity này được sử dụng trong chức năng Authentication.<br>
 * Sẽ được tạo thành JWT.
 */
@Data
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(name = "username", columnDefinition = "TEXT", unique = true)
    private String username;

    @Column(name = "email", columnDefinition = "TEXT", unique = true, nullable = false)
    private String email;

    @Column(name = "password", columnDefinition = "TEXT")
    private String password;

    @Column(name = "provider", columnDefinition = "TEXT")
    private String provider;

    @Column(name = "provider_id", columnDefinition = "TEXT")
    private String providerId;

    @Column(name = "full_name", columnDefinition = "TEXT")
    private String fullName;

    @Column(name = "avatar", columnDefinition = "TEXT")
    private String avatar;

    @Column(name = "last_login_at")
    private OffsetDateTime lastLoginAt;

    @Column(name = "first_name", columnDefinition = "TEXT")
    private String firstName;

    @Column(name = "middle_name", columnDefinition = "TEXT")
    private String middleName;

    @Column(name = "last_name", columnDefinition = "TEXT")
    private String lastName;

    @Column(name = "phone_number", columnDefinition = "TEXT")
    private String phoneNumber;

    @Column(name = "gender", columnDefinition = "TEXT")
    private String gender;

    @Column(name = "avatar_url", columnDefinition = "TEXT")
    private String avatarUrl;

    @Column(name = "smart_otp", columnDefinition = "TEXT")
    private String smartOtp;

    @Column(name = "date_of_birth")
    private OffsetDateTime dateOfBirth;

    @Column(name = "id_card_number", columnDefinition = "TEXT")
    private String idCardNumber;

    @Column(name = "id_card_front_img_url", columnDefinition = "TEXT")
    private String idCardFrontImgUrl;

    @Column(name = "id_card_back_img_url", columnDefinition = "TEXT")
    private String idCardBackImgUrl;

    @Column(name = "kyc_status")
    private Boolean kycStatus = false;

    @Column(name = "home_address", columnDefinition = "TEXT")
    private String homeAddress;

    @Column(name = "ward", columnDefinition = "TEXT")
    private String ward;

    @Column(name = "district", columnDefinition = "TEXT")
    private String district;

    @Column(name = "province", columnDefinition = "TEXT")
    private String province;

    @Column(name = "nation", columnDefinition = "TEXT")
    private String nation;

    @Column(name = "privy_id", columnDefinition = "TEXT")
    private String privyId;

    @Column(name = "wallet_address", columnDefinition = "TEXT")
    private String walletAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ranking_id")
    private Ranking ranking;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<DeviceInfo> devices;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Loan> loans;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<ReferralBonus> bonuses;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<SavingAccount> savings;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserBankAccount> bankAccounts;

//    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
//    private List<SubWallet> subWallets;

    @OneToOne(mappedBy = "user")
    private UserOtp userOtp;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, optional = true)
    private GoogleAuth googleAuth;


}