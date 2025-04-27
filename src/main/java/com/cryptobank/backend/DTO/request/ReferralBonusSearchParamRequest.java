package com.cryptobank.backend.DTO.request;

import io.swagger.v3.oas.annotations.Parameter;
import java.math.BigDecimal;
import lombok.Data;
import org.springdoc.core.annotations.ParameterObject;

@Data
@ParameterObject
public class ReferralBonusSearchParamRequest {

    @Parameter(description = "Email người giới thiệu")
    private String userReferralEmail;

    @Parameter(description = "Email người nhập mã")
    private String userEmail;

    @Parameter(description = "ID status")
    private String status;

    @Parameter(description = "Số lượng bonus. Ex: 100, 100-200")
    private String bonus;

}
