package com.example.BE_Crypto_Bank.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Table(name="ranking")
public class Ranking implements Serializable{
	@Id
	@Column(name="id")
	private String id;
	
	@Column(name="name")
	private String name;
	
	@Column(name = "is_activated")
	private Boolean is_activated;
	
	@Column(name="created_date")
	private LocalDateTime created_date;
	
	@Column(name="created_by")
	private String created_by;
	
	@Column(name="modified_date")
	private LocalDateTime modified_date;
	
	@Column(name="modified_by")
	private String modified_by;

	@JsonIgnore
	@OneToMany(mappedBy = "ranking", cascade = CascadeType.ALL)
	private List<User> customers;
	
}
