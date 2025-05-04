package com.cryptobank.backend.DTO.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class ReferralBonusCreateRequest {

    @NotBlank
    private String userReferralEmail;
    @NotBlank
    private String userId;
    @Digits(integer = 38, fraction = 2)
    @Min(1)
    private BigDecimal bonus;
    private String statusId;

}
