package com.cryptobank.backend.DTO.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleCreateRequest {

    @NotBlank
    private String name;
    private String note;
    private String status;

}
