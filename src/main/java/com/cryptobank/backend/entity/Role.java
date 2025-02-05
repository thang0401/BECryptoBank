package com.cryptobank.backend.entity;

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

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "role")
public class Role {

	@Id
	@Column(name="id")
	private String id;
	
	@Column(name="name")
	private String name;
	
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
	
	@Column(name="note")
	private String note;
	
	@ManyToOne
	@JoinColumn(name="status_id")
	private Status status;

	@JsonIgnore
	@OneToMany(mappedBy = "role")
	private List<RolePermission> role_permissions;

	@JsonIgnore
	@OneToMany(mappedBy = "role")
	private List<User> customers;

}
