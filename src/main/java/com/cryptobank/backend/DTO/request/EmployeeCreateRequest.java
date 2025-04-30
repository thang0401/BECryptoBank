package com.cryptobank.backend.DTO.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.Data;

@Data
public class EmployeeCreateRequest {

    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @Email
    private String email;
    @NotBlank
    private String firstName;
    private String middleName;
    @NotBlank
    private String lastName;
    private String phoneNumber;
    private OffsetDateTime hireDate = OffsetDateTime.now();
    @Digits(integer = 8, fraction = 2)
    @Min(1)
    private BigDecimal salary;
    private String taxCode;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String statusId;
    private String maritalStatusId;
    private String employmentTypeId;
    private String avatar;
    private String gender;
    private String address;

}
