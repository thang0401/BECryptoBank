package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "debit_account")
public class DebitAccount {

    @Id
    private String id;

    @Column(name = "user_id")
    private String user;

    @Column(name = "status")
    private String status;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name="balance")
    private Long balance;

}
