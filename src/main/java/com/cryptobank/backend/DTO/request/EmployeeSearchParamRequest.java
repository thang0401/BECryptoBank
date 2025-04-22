package com.cryptobank.backend.DTO.request;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;
import org.springdoc.core.annotations.ParameterObject;

@Data
@ParameterObject
public class EmployeeSearchParamRequest {

    @Parameter(description = "Email")
    private String email;
    @Parameter(description = "Tên")
    private String name;
    @Parameter(description = "Số điện thoại")
    private String phoneNumber;
    @Parameter(description = "Lương min-max. Ex: 1000, 1000-2000")
    private String salary;
    @Parameter(description = "Mã số thuế")
    private String taxCode;
    private String emergencyContactName;
    private String emergencyContactPhone;
    @Parameter(description = "ID của status")
    private String status;
    @Parameter(description = "ID của marital status")
    private String maritalStatus;
    @Parameter(description = "ID của employee type")
    private String employmentType;

}
