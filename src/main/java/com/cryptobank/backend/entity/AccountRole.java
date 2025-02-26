package com.cryptobank.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "account_role")
public class AccountRole extends BaseEntity {

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name ="role_id")
    private Role role;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name ="user_id")
    private User user;

    @Column(name = "is_activated")
    private boolean activated;

}
