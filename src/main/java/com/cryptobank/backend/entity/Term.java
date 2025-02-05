package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "term")
public class Term {
    @Id
    private String id;

    @Column(name = "amount_term")
    private Integer amount_term;

    @Column(name = "type")
    private String type;

    @Column(name = "percent_of_year")
    private String percent_of_year;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @Column(name = "modified_by")
    private String modifiedBy;

    @JsonIgnore
    @OneToMany(mappedBy = "term")
    private List<UserPortfolio> list_portfolio;
}
