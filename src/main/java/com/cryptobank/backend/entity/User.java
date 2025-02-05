package com.cryptobank.backend.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "user_name")
    private String username;

    @Column(name = "phone_num")
    private String phone;

    @Column(name = "gender")
    private String gender;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "avatar_url")
    private String avatarURL;

    @Column(name = "password")
    private String password;

    @Column(name = "smart_otp")
    private String smartOTP;

    @Column(name = "google_id")
    private String googleId;

    @Column(name = "date_of_birth")
    private LocalDateTime dateOfBirth;

    @Column(name = "id_number")
    private String idNumber;

    @Column(name = "id_card_front_img_url")
    private String idCardFrontImgURL;

    @Column(name = "id_card_back_img_url")
    private String idCardBackImgURL;

    @Column(name = "is_activated")
    private boolean activated;

    @Column(name = "delete_yn")
    private boolean deleted;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @Column(name = "modified_by")
    private String modifiedBy;

    @Column(name = "type_sign_in")
    private String type_sign_in;

    @Column(name = "kyc_status")
    private Boolean kyc_status;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne
    @JoinColumn(name="address_id")
    private Address address;

    @ManyToOne
    @JoinColumn(name = "ranking_id")
    private Ranking ranking;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private Status status;
}
