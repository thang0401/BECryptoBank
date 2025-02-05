package com.example.BE_Crypto_Bank.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Embeddable
public class Role_permission_key {

    @Column(name="role_id")
    private String role_id;

    @Column(name="permission_id")
    private String permission_id;

}
