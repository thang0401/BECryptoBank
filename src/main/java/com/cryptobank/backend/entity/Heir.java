package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "heir")
public class Heir {

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "name")
	private String name;

	@ManyToOne
	@JoinColumn(name = "portfolio_id")
	private Portfolio portfolio;

	@Column(name = "id_card")
	private String idCard;

	@Column(name = "last_words")
	private String lastWords;

	@Column(name = "date_of_birth")
	private LocalDateTime dateOfBirth;

	@Column(name = "delete_yn")
	private boolean deleted;

	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "modified_date")
	private LocalDateTime modifiedDate;

	@Column(name = "modified_by")
	private String modifiedBy;

}
