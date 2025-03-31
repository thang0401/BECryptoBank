package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@Entity
@Table(name = "group_status")
public class GroupStatus extends BaseEntity {

    @Column(name = "name", columnDefinition = "TEXT")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private Status status;

    @JsonIgnore
    @OneToMany(mappedBy = "groupStatus")
    private List<Status> statuses = new ArrayList<>();

}