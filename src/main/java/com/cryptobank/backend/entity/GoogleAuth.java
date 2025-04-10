package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "google_auth")
@Data
public class GoogleAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "google_id", nullable = false, unique = true)
    private String googleId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}