package com.cryptobank.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "ranking")
public class Ranking extends BaseEntity {

    @Column(name = "name", columnDefinition = "TEXT")
    private String name;

    @OneToMany(mappedBy = "ranking")
    private List<User> users = new ArrayList<>();

}