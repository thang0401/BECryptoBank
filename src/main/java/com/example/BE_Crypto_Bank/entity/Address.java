package com.example.BE_Crypto_Bank.entity;

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

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="address_detail")
public class Address {
	@Id
	@Column(name="id")
	private String id;
	
	@Column(name="home_address")
	private String home_address;
	
	@Column(name="nation")
	private String nation;
	
	@Column(name="province")
	private String province;
	
	@Column(name="district")
	private String district;
	
	@Column(name="ward")
	private String ward;
	
	@Column(name="is_activated")
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
	@OneToMany(mappedBy = "address",cascade = CascadeType.ALL)
	private List<User> users;
}
