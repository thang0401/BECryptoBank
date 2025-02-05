package com.cryptobank.backend.entity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "portfolio_category")
public class PortfolioCategory implements Serializable {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "delete_yn")
    private boolean deleted;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @Column(name = "modified_by")
    private String modifiedBy;

 // map to table status
 	@ManyToOne
 	@JoinColumn(name = "status_id")
 	private Status portfolioCategoryStatus;

 	@JsonIgnore
 	@OneToMany(mappedBy = "portfolioCategory", cascade = CascadeType.ALL)
 	private List<UserPortfolio> userPortfolios;
}
