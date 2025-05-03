package com.cryptobank.backend.DTO;

import jakarta.validation.constraints.Digits;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
/**
 * DTO for {@link com.cryptobank.backend.entity.Employee}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTOChangePass {
    String id;
    String username;
    String email;
    String fullName;
    String phoneNumber;
    OffsetDateTime hireDate;
    OffsetDateTime terminationDate;
    @Digits(integer = 8, fraction = 2)
    BigDecimal salary;
    @Digits(integer = 10, fraction = 2)
    BigDecimal bonus;
    String insuranceNumber;
    String taxCode;
    String emergencyContactName;
    String emergencyContactPhone;
    String maritalStatus;
    String status;
    String employmentType;
}
