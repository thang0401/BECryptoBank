package com.example.BE_Crypto_Bank.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import org.springframework.cglib.core.Local;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="role_permission")
public class Role_permission implements Serializable {

	@EmbeddedId
	Role_permission_key id;

	@ManyToOne
	@MapsId("role_id")
	@JoinColumn(name = "role_id")
	private Role role;

	@ManyToOne
	@MapsId("permission_id")
	@JoinColumn(name = "permission_id")
	private Permission permission;
	
	@Column(name="created_date")
	private LocalDateTime created_date;

}
