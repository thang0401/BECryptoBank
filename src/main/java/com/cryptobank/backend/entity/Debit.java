package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "debit")
public class Debit {

    @Id
    private String id;
    private String note;

    @ManyToOne
    @JoinColumn(name = "received_user_id")
    private User receivedUser;

    @ManyToOne
    @JoinColumn(name = "send_user_id")
    private User sentUser;

    @ManyToOne
    @JoinColumn(name = "status")
    private Status status;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "created_by")
    private String createdBy;

}
