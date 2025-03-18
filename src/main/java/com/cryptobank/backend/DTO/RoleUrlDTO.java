package com.cryptobank.backend.DTO;

import lombok.Value;

import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * DTO for {@link com.cryptobank.backend.entity.RoleUrl}
 */
@Value
public class RoleUrlDTO implements Serializable {
    String id;
    OffsetDateTime createdAt;
    String createdBy;
    OffsetDateTime modifiedAt;
    String modifiedBy;
    String functionUrl;
    String roleName;
}