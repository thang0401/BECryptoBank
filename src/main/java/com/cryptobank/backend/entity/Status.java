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

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "status")
public class Status implements Serializable{

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "note")
    private String note;

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
    
    @JsonIgnore
	@OneToMany(mappedBy = "status", cascade = CascadeType.ALL)
	private List<User> customers;

	@JsonIgnore
	@OneToMany(mappedBy = "status", cascade = CascadeType.ALL)
	private List<Role> roles;
	
	@JsonIgnore
	@OneToMany(mappedBy = "portfolioCategoryStatus", cascade = CascadeType.ALL)
	private List<PortfolioCategory> portfolioCategories;

}
