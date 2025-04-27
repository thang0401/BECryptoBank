package com.cryptobank.backend.DTO;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class KycRequest {
	private String fullName;
	private String firstName;
	private String middleName;
	private String lastName;
	private String address;
	private OffsetDateTime dateOfBirth;
	private String gender;
	private String phone;
	private String idCardFrontImgUrl;
	private String idCardBackImgUrl;
	private String ward;
	private String district;
	private String province;
	private String nation;
	private String idNumber;
}
