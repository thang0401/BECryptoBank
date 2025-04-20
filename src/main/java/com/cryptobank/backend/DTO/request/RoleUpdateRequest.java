package com.cryptobank.backend.DTO.request;

import com.cryptobank.backend.entity.Role;
import lombok.Data;

@Data
public class RoleUpdateRequest {

    private String name;
    private String note;
    private String statusId;

    public boolean isSimilar(Role role) {
        return (name != null && name.equals(role.getName())) &&
            (note != null && note.equals(role.getNote())) &&
            (statusId != null && statusId.equals(role.getStatus().getId()));
    }

}
