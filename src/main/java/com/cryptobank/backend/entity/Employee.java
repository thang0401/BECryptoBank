package com.cryptobank.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "employee")
public class Employee extends BaseEntity {

    @Column(name = "username", columnDefinition = "TEXT", unique = true)
    private String username;

    @Column(name = "email", columnDefinition = "TEXT", unique = true, nullable = false)
    private String email;

    @Column(name = "password", columnDefinition = "TEXT")
    private String password;

    @Column(name = "first_name", columnDefinition = "TEXT")
    private String firstName;

    @Column(name = "middle_name", columnDefinition = "TEXT")
    private String middleName;

    @Column(name = "last_name", columnDefinition = "TEXT")
    private String lastName;

    @Column(name = "phone_number", columnDefinition = "TEXT")
    private String phoneNumber;

    @Column(name = "hire_date")
    private OffsetDateTime hireDate;

    @Column(name = "termination_date")
    private OffsetDateTime terminationDate;

    @Column(name = "salary", nullable = false)
    @DecimalMin(value = "0")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal salary = BigDecimal.ZERO;

    @Column(name = "bonus")
    @DecimalMin(value = "0")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal bonus = BigDecimal.ZERO;

    @Column(name = "is_change_pass")
    private boolean isChangePass = false;

    @Column(name = "insurance_number", columnDefinition = "TEXT", unique = true)
    private String insuranceNumber;

    @Column(name = "tax_code", columnDefinition = "TEXT", unique = true)
    private String taxCode;

    @Column(name = "emergency_contact_name", columnDefinition = "TEXT")
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone", columnDefinition = "TEXT")
    private String emergencyContactPhone;

    @Column(name = "avatar", columnDefinition = "TEXT")
    private String avatar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marital_status")
    private Status maritalStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employment_type_id")
    private EmploymentType employmentType;

}
