package com.cryptobank.backend.DTO;

import lombok.Data;

@Data
public class RoleUpdateRequest {

    private String name;
    private String note;
    private String status;

}
