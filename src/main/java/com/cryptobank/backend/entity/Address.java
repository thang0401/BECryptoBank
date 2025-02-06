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
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "address_detail")
public class Address {

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "home_address")
	private String address;

	@Column(name = "nation")
	private String nation;

	@Column(name = "province")
	private String province;

	@Column(name = "district")
	private String district;

	@Column(name = "ward")
	private String ward;

	@Column(name = "is_activated")
	private boolean activated;

	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "modified_date")
	private LocalDateTime modifiedDate;

	@Column(name = "modified_by")
	private String modifiedBy;

	@JsonIgnore
	@OneToMany(mappedBy = "address",cascade = CascadeType.ALL)
	private List<User> users;
}
