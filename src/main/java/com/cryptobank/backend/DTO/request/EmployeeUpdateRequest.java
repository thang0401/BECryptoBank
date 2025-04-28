package com.cryptobank.backend.DTO.request;

import com.cryptobank.backend.entity.Employee;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.Data;

@Data
public class EmployeeUpdateRequest {

    private String password;
    @Email
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private String phoneNumber;
    private String avatar;
    @Digits(integer = 8, fraction = 2)
    private BigDecimal salary;
    @Digits(integer = 10, fraction = 2)
    private BigDecimal bonus;
    private String taxCode;
    private OffsetDateTime terminationDate;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String statusId;
    private String maritalStatusId;
    private String employmentTypeId;
    
    public boolean isSimilar(Employee employee) {
        return (password != null && password.equals(employee.getPassword())) &&
            (email != null && email.equals(employee.getEmail())) &&
            (firstName != null && firstName.equals(employee.getFirstName())) &&
            (middleName != null && middleName.equals(employee.getMiddleName())) &&
            (lastName != null && lastName.equals(employee.getLastName())) &&
            (phoneNumber != null && phoneNumber.equals(employee.getPhoneNumber())) &&
            (avatar != null && avatar.equals(employee.getAvatar())) &&
            (salary != null && salary.equals(employee.getSalary())) &&
            (bonus != null && bonus.equals(employee.getBonus())) &&
            (taxCode != null && taxCode.equals(employee.getTaxCode())) &&
            (emergencyContactName != null &&
                emergencyContactName.equals(employee.getEmergencyContactName())) &&
            (emergencyContactPhone != null &&
                emergencyContactPhone.equals(employee.getEmergencyContactPhone())) &&
            (statusId != null && statusId.equals(employee.getStatus().getId())) &&
            (maritalStatusId != null && maritalStatusId.equals(employee.getMaritalStatus().getId())) &&
            (employmentTypeId != null && employmentTypeId.equals(employee.getEmploymentType().getId()));
    }

}
