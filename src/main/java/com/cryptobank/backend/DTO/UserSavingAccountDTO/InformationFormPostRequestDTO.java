package com.cryptobank.backend.DTO.UserSavingAccountDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InformationFormPostRequestDTO {
    private String debitAccountId;
    private Double amount;
    private String termId;
    @JsonProperty("OTP")
    private Integer OTP;
}
