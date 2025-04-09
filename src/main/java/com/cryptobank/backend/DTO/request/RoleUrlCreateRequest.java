package com.cryptobank.backend.DTO.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleUrlCreateRequest {

    @NotBlank
    private String roleId;
    @NotBlank
    private String url;

}
