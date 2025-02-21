package com.cryptobank.backend.entity;

import java.time.ZonedDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SubWallet")
public class SubWallet {

    @Id
    private String id;

    @Column(name="user_id")
    private String userId;

    @Column(name="address")
    private String address;

    @Column(name = "privateKey")
    private String privateKey;

    @Column(name="created_at")
    private ZonedDateTime createdAt;

  
}
