package com.cryptobank.backend.DTO.UserSavingAccountDTO;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InformationFormPostRequestDTO {
    private BigDecimal amount;
    private String termId;
}
