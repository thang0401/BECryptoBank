package com.cryptobank.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminSavingAccountDTO {
    private String id;
    private String user_id;
    private String user_firstname;
    private String user_lastname;
    private Boolean heirStatus;
    private BigDecimal balance;
    private Long amount_month;
    private String type;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    }
