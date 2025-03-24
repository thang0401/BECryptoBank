package com.cryptobank.backend.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Past;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String firstName;

    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private String lastName;

    @Pattern(regexp = "^\\d{10,15}$", message = "Phone number must be 10-15 digits")
    private String phoneNumber;

    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$", message = "Gender must be MALE, FEMALE, or OTHER")
    private String gender;

    @Past(message = "Date of birth must be in the past")
    private OffsetDateTime dateOfBirth;

    @Size(max = 200, message = "Home address cannot exceed 200 characters")
    private String homeAddress;

    @Size(max = 50, message = "Ward cannot exceed 50 characters")
    private String ward;

    @Size(max = 50, message = "District cannot exceed 50 characters")
    private String district;

    @Size(max = 50, message = "Province cannot exceed 50 characters")
    private String province;

    @Size(max = 50, message = "Nation cannot exceed 50 characters")
    private String nation;
}