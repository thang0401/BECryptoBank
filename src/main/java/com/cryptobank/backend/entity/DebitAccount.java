package com.cryptobank.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "debit_account")
public class DebitAccount extends BaseEntity {

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "status")
    private String status;

    @Column(name="balance")
    private Long balance;

    @Column(name = "account_number")
    private String accountNumber;

    @OneToMany(mappedBy = "debitAccount" , cascade = CascadeType.ALL)
    private List<DebitDetail> transactions;

}
