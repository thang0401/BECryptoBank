package com.cryptobank.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "employment_type")
public class EmploymentType extends BaseEntity {

    @Column(name = "name", columnDefinition = "text")
    private String type_name;

    @OneToMany(mappedBy = "employmentType")
    private List<Employee> employees = new ArrayList<>();

}
