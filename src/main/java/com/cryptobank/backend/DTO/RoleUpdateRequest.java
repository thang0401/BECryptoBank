package com.cryptobank.backend.DTO;

import com.cryptobank.backend.entity.Status;

import lombok.Data;

@Data
public class RoleUpdateRequest {

    private String name;
    private String note;
    private Status status;

}
