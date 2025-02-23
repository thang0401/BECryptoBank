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
public class Heir extends BaseEntity {

	@Column(name = "name")
	private String name;

	@Column(name="saving_account")
	private String savingAccount;

	@Column(name="gg_drive_url")
	private String googleDriveURL;

}
