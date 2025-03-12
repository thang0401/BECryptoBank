package com.cryptobank.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TermAddDTO {
    private String id;
    private Long amountMonth;
    private String type;
    private String createdBy="system";
    private Double interestRate;
}
