package com.cryptobank.backend.DTO;

import java.time.OffsetDateTime;

import lombok.Data;

@Data
public class KycRequest {
	private String fullName;
	private String firstName;
	private String middleName;
	private String lastName;
	private String email;
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
