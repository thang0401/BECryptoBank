package com.cryptobank.backend.entity;

import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavingSmartcontractInput  {
    private String savingAccountId;
    private String term;
    private BigInteger startDate;
    private BigInteger endDate;
    private String supportStaff;
    private String ggDriveUrl;
    private String ownerName;
    private String ownerId;
    private String email;
    private String phone;
    private String status;
    private String heirStatus;
}
