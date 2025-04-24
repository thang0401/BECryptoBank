package com.cryptobank.backend.DTO.request;

import com.cryptobank.backend.entity.ReferralBonus;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class ReferralBonusUpdateRequest {

    private String userReferralId;
    private String userId;
    @Digits(integer = 38, fraction = 2)
    @Min(1)
    private BigDecimal bonus;
    private String statusId;

    public boolean isSimilar(ReferralBonus referralBonus) {
        return (userId != null && userId.equals(referralBonus.getUser().getId())) &&
            (userReferralId != null && userReferralId.equals(referralBonus.getReferralUser().getId())) &&
            (bonus != null && bonus.compareTo(referralBonus.getBonusAmount()) == 0) &&
            (statusId != null && statusId.equals(referralBonus.getStatus().getId()));
    }

}
