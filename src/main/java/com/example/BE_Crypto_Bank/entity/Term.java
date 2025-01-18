package com.example.BE_Crypto_Bank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
@Table(name="term")
public class Term {
	@Id
	private String id;
	
	@Column(name="amount_term")
	private Integer amount_term;
	
	@Column(name="type")
	private String type;
	
	@Column(name="percent_of_year")
	private String percent_of_year;
	
	@Column(name="created_date")
	private LocalDateTime created_date;
	
	@Column(name="created_by")
	private String created_by;
	
	@Column(name="modified_date")
	private LocalDateTime modified_date;
	
	@Column(name="modified_by")
	private String modified_by;
	
	@JsonIgnore
	@OneToMany(mappedBy = "term")
	private List<User_portfolio> list_portfolio;
}
