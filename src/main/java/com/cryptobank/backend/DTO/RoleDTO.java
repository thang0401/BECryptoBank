package com.cryptobank.backend.DTO;

import lombok.Value;

import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * DTO for {@link com.cryptobank.backend.entity.Role}
 */
@Value
public class RoleDTO implements Serializable {
    String id;
    OffsetDateTime createdAt;
    String createdBy;
    OffsetDateTime modifiedAt;
    String modifiedBy;
    String name;
    String note;
    String statusName;
}