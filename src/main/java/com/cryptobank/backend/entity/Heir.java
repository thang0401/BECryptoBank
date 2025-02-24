package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

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

	@Column(name="saving_account")
	private String savingAccount;

	@Column(name="gg_drive_url")
	private String GoogleDriveURL;

	@Column(name="delete_yn")
	private Boolean isDeleted;

	@Column(name = "created_date")
	private ZonedDateTime createdDate;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "modified_date")
	private ZonedDateTime modifiedDate;

	@Column(name = "modified_by")
	private String modifiedBy;

}
