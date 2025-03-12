package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Entity này được sử dụng trong chức năng Authentication.<br>
 * Sẽ được tạo thành JWT.
 */
@Getter
@Setter
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

    @Column(name = "has_accepted_terms")
    private Boolean hasAcceptedTerms = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ranking_id")
    private Ranking ranking;

    @OneToMany(mappedBy = "user")
    private List<UserRole> userRoles = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<DebitWallet> debitWallets = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<DeviceInfo> deviceInfoes = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Loan> loans = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Passkey> passkeys = new ArrayList<>();

    @OneToMany(mappedBy = "beReferralUser")
    private List<ReferralBonus> beReferralBonuses = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<SavingAccount> savingAccounts = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<SavingTransaction> savingTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "beReferralUser", orphanRemoval = true)
    private List<ReferralBonus> referralBonuses = new ArrayList<>();

}
