package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import java.math.BigDecimal;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

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

    @Column(name = "has_accepted_terms")
    private Boolean hasAcceptedTerms = false;

    @Column(name = "hire_date")
    private OffsetDateTime hireDate;

    @Column(name = "termination_date")
    private OffsetDateTime terminationDate;

    @Column(name = "salary", nullable = false)
    @DecimalMin(value = "0")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal salary = BigDecimal.ZERO;

    @Column(name = "bonus")
    @DecimalMin(value = "0")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal bonus = BigDecimal.ZERO;

    @Column(name = "insurance_number", columnDefinition = "text", unique = true)
    private String insuranceNumber;

    @Column(name = "tax_code", columnDefinition = "text", unique = true)
    private String taxCode;

    @Column(name = "emergency_contact_name")
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone")
    private String emergencyContactPhone;

    @ManyToOne
    @JoinColumn(name = "marital_status")
    private Status maritalStatus;

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
    

}
