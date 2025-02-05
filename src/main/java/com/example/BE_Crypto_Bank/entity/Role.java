package com.example.BE_Crypto_Bank.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="role")
public class Role implements Serializable{
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
	private List<Role_permission> role_permissions;

	@JsonIgnore
	@OneToMany(mappedBy = "role")
	private List<User> customers;

}
