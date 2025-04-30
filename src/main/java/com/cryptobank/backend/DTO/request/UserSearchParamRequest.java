package com.cryptobank.backend.DTO.request;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;
import org.springdoc.core.annotations.ParameterObject;

@Data
@ParameterObject
public class UserSearchParamRequest {

    @Parameter(description = "Số điện thoại")
    private String phone;

    @Parameter(description = "Email")
    private String email;

    @Parameter(description = "Tên người dùng")
    private String name;

    @Parameter(description = "ID của role")
    private String role;

    @Parameter(description = "ID của status")
    private String status;

    @Parameter(description = "ID của ranking")
    private String ranking;

}
