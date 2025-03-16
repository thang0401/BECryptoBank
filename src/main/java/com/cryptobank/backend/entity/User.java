package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Entity này được sử dụng trong chức năng Authentication.<br>
 * Sẽ được tạo thành JWT.
 */
@Getter
@Setter
@Entity
@Builder
@Table(name = "users")
public class User extends BaseEntity implements Serializable {

    @Column(name = "username", columnDefinition = "TEXT", unique = true)
    private String username;
    
    @Column(name = "password", columnDefinition = "TEXT")
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name="role_id")
    private String roleId;

    @Column(name = "phone_num")
    private String phone;

    @Column(name = "gender")
    private String gender;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "avatar_url")
    private String avatarURL;

    @Column(name="status_id")
    private String statusId;

    @Column(name="ranking_id")
    private String rankingId;

    @Column(name = "smart_otp")
    private String smartOTP;

    @Column(name = "id_number")
    private String idNumber;

    @Column(name = "google_id")
    private String googleId;

    @Column(name="address_id")
    private String addressId;

    @Column(name = "date_of_birth")
    private ZonedDateTime dateOfBirth;

    @Column(name = "id_card_front_img_url")
    private String idCardFrontImgURL;

    @Column(name = "id_card_back_img_url")
    private String idCardBackImgURL;

    @Column(name = "is_activated")
    private Boolean activated;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "modified_date")
    private ZonedDateTime modifiedDate;

    @Column(name = "modified_by")
    private String modifiedBy;

    @Column(name = "type_sign_in")
    private String type_sign_in;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "kyc_status")
    private Boolean kyc_status;

    @Column(name="delete_yn")
    private Boolean isDeleted;

    @Column(name="ward")
    private String ward;

    @Column(name="district")
    private String district;

    @Column(name="province")
    private String province;

    @Column(name="nation")
    private String nation;

    @Column(name = "privy_id")
    private String privyId;

    @Column(name = "wallet_address")
    private String walletAddress;

    @Column(name = "has_accepted_terms")
    private Boolean hasAcceptedTerms;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    @JsonIgnore
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ranking_id")
    @JsonIgnore
    private Ranking ranking;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<UserRole> userRoles = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<DebitWallet> debitWallets = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<DeviceInfo> deviceInfoes = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Loan> loans = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Passkey> passkeys = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "beReferralUser")
    private List<ReferralBonus> beReferralBonuses = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<SavingAccount> savingAccounts = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<SavingTransaction> savingTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<SubWallet> subWallets;
}