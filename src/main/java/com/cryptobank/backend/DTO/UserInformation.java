package com.cryptobank.backend.DTO;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInformation {
	private String id;
	private String username;
    private String email;
    private String fullName;
    private String firstName;
    private String middleName;
    private String lastName;
    private String phoneNumber;
    private String gender;
    private String avatar;
    private OffsetDateTime dateOfBirth;
    private String homeAddress;
    private String ward;
    private String district;
    private String province;
    private String nation;
    private String walletAddress;
    private Boolean kycStatus;
    private Boolean hasAcceptedTerms;
    private OffsetDateTime lastLoginAt;
    private Boolean  isBankAccount;
    private Boolean  isReferralCode;

}
