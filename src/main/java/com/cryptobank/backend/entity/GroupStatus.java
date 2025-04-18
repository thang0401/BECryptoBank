package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Entity
@Table(name = "group_status")
public class GroupStatus extends BaseEntity {

    @Column(name = "name", columnDefinition = "TEXT")
    private String name;

    @OneToMany(mappedBy = "groupStatus")
    private List<Status> statuses = new ArrayList<>();

}