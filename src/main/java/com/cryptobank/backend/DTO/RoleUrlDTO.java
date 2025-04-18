package com.cryptobank.backend.DTO;

import lombok.Value;

/**
 * DTO for {@link com.cryptobank.backend.entity.RoleUrl}
 */
@Value
public class RoleUrlDTO {
    String id;
    String url;
    String role;
}