package com.cryptobank.backend.DTO.supportDTOs.responseDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllDTO {
    private String id;
    private String title;
    private String issue;
    private String reported_by;
    private String created_date;
    private int priority;
    private String status;
}
