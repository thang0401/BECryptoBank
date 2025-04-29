package com.cryptobank.backend.DTO.supportDTOs.responseDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserListIssueResponseDTO {
    private String title;
    private String created_date;
    private String issue;
    private String status;
}
