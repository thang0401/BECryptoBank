package com.cryptobank.backend.DTO.UserSavingAccountDTO;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSavingGetAllResponse {
    private String accountId;
    private String userId;
    private String userName;
    private String userEmail;
    private String userPhone;
    private Boolean isHeir;
    private BigDecimal balance;
    private Long term;
    private String startDate;
    private String endDate;
    private String status;

}
