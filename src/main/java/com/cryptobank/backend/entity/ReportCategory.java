package com.cryptobank.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="report_category")
public class ReportCategory {
    @Id
    @Column(name="id")
    private String Id;
    @Column(name="title")
    private String Title;
    @Column(name="issue")
    private String Issue;
}
