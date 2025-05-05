package com.cryptobank.backend.DTO;

import lombok.Data;

@Data
public class InternalTransactionRequest {
    private String fromAccountId;
    private String toAccountId;
    private String amount;
    private String note;
}