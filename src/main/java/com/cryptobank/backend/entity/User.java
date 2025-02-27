package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User extends BaseEntity {

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

    @Column(name = "password")
    private String password;

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

    @Column(name = "type_sign_in")
    private String type_sign_in;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "user_name")
    private String username;

    @Column(name = "kyc_status")
    private Boolean kyc_status;

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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<AccountRole> roles;

    @OneToMany(mappedBy= "user",cascade = CascadeType.ALL)
    private List<DebitAccount> debitAccounts;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<DeviceInfo> devices;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Loan> loans;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<ReferralBonus> bonuses;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<SavingAccount> savings;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<SubWallet> subWallets;
}
