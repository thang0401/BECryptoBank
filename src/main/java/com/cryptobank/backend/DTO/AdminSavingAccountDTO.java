package com.cryptobank.backend.DTO;

import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminSavingAccountDTO {
    private String id;
    private String user_id;
    private String user_firstname;
    private String user_lastname;
    private Boolean heirStatus;
    private Double balance;
    private Long amount_month;
    private String type;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    }
