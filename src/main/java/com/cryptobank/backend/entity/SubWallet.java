package com.cryptobank.backend.entity;

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
    private String address;

    @Column(name = "privateKey")
    private String privateKey;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

}
