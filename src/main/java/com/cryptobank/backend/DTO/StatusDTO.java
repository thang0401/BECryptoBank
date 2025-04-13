package com.cryptobank.backend.DTO;

import lombok.Value;

/**
 * DTO for {@link com.cryptobank.backend.entity.Status}
 */
@Value
public class StatusDTO {
    String id;
    String name;
    String group;
}
