package com.cryptobank.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "employment_type")
public class EmploymentType extends BaseEntity {

    @Column(name = "name", columnDefinition = "text")
    private String type_name;

}
