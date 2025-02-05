package com.example.BE_Crypto_Bank.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="permission")
public class Permission implements Serializable{
	@Id
	@Column(name="id")
	private String id;
	
	@Column(name="name")
	private String name;
	
	@Column(name="all_yn")
	private Boolean all_yn;
	
	@Column(name="insert_yn")
	private Boolean insert_yn;
	
	@Column(name="update_yn")
	private Boolean update_yn;
	
	@Column(name="delete_yn")
	private Boolean delete_yn;
	
	@Column(name="view_yn")
	private Boolean view_yn;
	
	@Column(name="view_all_yn")
	private Boolean view_all_yn;
	
	@Column(name="excel_yn")
	private Boolean excel_yn;
	
	@Column(name="delete_permission_yn")
	private Boolean delete_permission_yn;
	
	@Column(name="created_date")
	private LocalDateTime created_date;
	
	@Column(name="created_by")
	private String created_by;
	
	@Column(name="modified_date")
	private LocalDateTime modified_date;
	
	@Column(name="modified_by")
	private String modified_by;

	@JsonIgnore
	@OneToMany(mappedBy = "permission")
	private List<Role_permission> role_permissions;

}
