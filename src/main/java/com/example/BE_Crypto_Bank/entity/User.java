package com.example.BE_Crypto_Bank.entity;


import jakarta.persistence.*;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import java.time.LocalDateTime;

@SuppressWarnings("serial")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements Serializable{
    @Id
    @Column(name = "id")
    private String id;
    
    @Column(name="first_name")
    private String first_name;

    @Column(name="last_name")
    private String last_name;

    @Column(name="user_name")
    private String user_name;
    
    @Column(name="phone_num")
    private String phone_num;
    
    @Column(name="gender")
    private String gender;
    
    @Column(name="email",nullable = false, unique = true) 
    private String email;
    
    @Column(name="avatar_url")
    private String avatar_url;
    
    @Column(name="password")
    private String password;
    
    @Column(name="smart_otp")
    private String smart_otp;
    
    @Column(name="google_id")
    private String google_id;
    
    @Column(name="date_of_birth")
    private LocalDateTime date_of_birth;

    @Column(name="id_number")
    private String id_number;
    
    @Column(name="id_card_front_img_url")
    private String id_card_front_img_url;
    
    @Column(name="id_card_back_img_url")
    private String id_card_back_img_url;
    
    @Column(name="is_activated")
    private Boolean is_activated;
    
    @Column(name = "delete_yn")
    private Boolean deleteYn;

    @Column(name = "created_date")
    private LocalDateTime created_date;

    @Column(name = "created_by")
    private String created_by;

    @Column(name = "modified_date")
    private LocalDateTime modified_date ;

    @Column(name = "modified_by")
    private String  modified_by;
    
    @Column(name="type_sign_in")
    private String type_sign_in;
    
    @Column(name="kyc_status")
    private Boolean kyc_status;


    @ManyToOne
    @JoinColumn(name="role_id")
    private Role role;

    @ManyToOne
    @JoinColumn(name="address_id")
    private Address address;

    @ManyToOne
    @JoinColumn(name="ranking_id")
    private Ranking ranking;

    @ManyToOne
    @JoinColumn(name="status_id")
    private Status status;
}
