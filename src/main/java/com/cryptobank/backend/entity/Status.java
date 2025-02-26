package com.cryptobank.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "status")
public class Status extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "note")
    private String note;

    @Column(name="group_id")
    private String groupId;

}
