package com.cryptobank.backend.DTO;

import com.cryptobank.backend.entity.Status;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleCreateRequest {

    @NotBlank
    private String name;
    private String note;
    @NotBlank
    private Status status;

}
