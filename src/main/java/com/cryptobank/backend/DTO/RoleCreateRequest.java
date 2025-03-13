package com.cryptobank.backend.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleCreateRequest {

    @NotBlank
    private String name;
    private String note;
    @NotBlank
    private String status;

}
