package com.cryptobank.backend.DTO;

import java.math.BigDecimal;
import lombok.Value;

@Value
public class ReferralBonusDTO {
    String id;
    BigDecimal bonusAmount;
    String status;
    UserReferralBonusDTO user;
    UserReferralBonusDTO referralUser;

    @Value
    public static class UserReferralBonusDTO {
        String id;
        String fullName;
        String email;
    }
}
