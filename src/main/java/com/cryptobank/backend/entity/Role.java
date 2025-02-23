package com.cryptobank.backend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "role")
public class Role extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name="status_id")
    private String statusId;

    @Column(name = "is_activated")
    private boolean activated;

    @Column(name = "note")
    private String note;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    private List<AccountRole> accounts;

}
