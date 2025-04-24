package com.cryptobank.backend.DTO;

import java.math.BigDecimal;
import lombok.Value;

@Value
public class ReferralBonusDTO {
    String id;
    String user;
    BigDecimal bonusAmount;
    String status;
    String referralUser;
}
