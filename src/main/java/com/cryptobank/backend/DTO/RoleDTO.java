package com.cryptobank.backend.DTO;

import lombok.Value;

/**
 * DTO for {@link com.cryptobank.backend.entity.Role}
 */
@Value
public class RoleDTO {
    String id;
    String name;
    String note;
    String status;
}