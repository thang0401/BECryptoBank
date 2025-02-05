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

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "status")
public class Status {

	@Id
	@Column(name="id")
	private String id;
	
	@Column(name="name")
	private String name;
		
	@Column(name="note")
	private String note;
	
	@Column(name="delete_yn")
	private Boolean delete_yn;
	
	@Column(name="created_date")
	private LocalDateTime created_date;
	
	@Column(name="created_by")
	private String created_by;
	
	@Column(name="modified_date")
	private LocalDateTime modified_date;
	
	@Column(name="modified_by")
	private String modified_by;

	@JsonIgnore
	@OneToMany(mappedBy = "status", cascade = CascadeType.ALL)
	private List<User> customers;

	@JsonIgnore
	@OneToMany(mappedBy = "status", cascade = CascadeType.ALL)
	private List<Role> roles;
	
	@JsonIgnore
	@OneToMany(mappedBy = "portfolio_category_status", cascade = CascadeType.ALL)
	private List<PortfolioCategory> portfolio_categories;

}
