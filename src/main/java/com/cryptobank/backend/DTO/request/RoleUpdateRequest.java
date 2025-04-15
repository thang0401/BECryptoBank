package com.cryptobank.backend.DTO.request;

import lombok.Data;

@Data
public class RoleUpdateRequest {

    private String name;
    private String note;
    private String statusId;

}
