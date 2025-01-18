package com.example.BE_Crypto_Bank.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "portfolio")
public class User_portfolio implements Serializable {
	@Id
	@Column(name = "id")
	private String id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user_id;

	@ManyToOne
	@JoinColumn(name = "portfolio_id")
	private Portfolio_category portfolioCategory;

	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@Column(name = "modified_date")
	private LocalDateTime modifiedDate;

	@Column(name = "status")
	private String status;

	@ManyToOne
	@JoinColumn(name = "term_id")
	private Term term;
}
