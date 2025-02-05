package com.example.BE_Crypto_Bank.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "portfolio_category")
public class Portfolio_category implements Serializable {
	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "name")
	private String name;

	@Column(name = "delete_yn")
	private Boolean delete_yn;

	@Column(name = "created_date")
	private LocalDateTime created_date;

	@Column(name = "created_by")
	private String created_by;

	@Column(name = "modified_date")
	private LocalDateTime modified_datel;

	@Column(name = "modified_by")
	private String modified_by;

	// map to table status
	@ManyToOne
	@JoinColumn(name = "status_id")
	private Status portfolio_category_status;

	@JsonIgnore
	@OneToMany(mappedBy = "portfolioCategory", cascade = CascadeType.ALL)
	private List<User_portfolio> userPortfolios;
}
