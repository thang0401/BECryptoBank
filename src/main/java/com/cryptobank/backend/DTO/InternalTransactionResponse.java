package com.cryptobank.backend.DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class InternalTransactionResponse {
    private String transactionId;
    private String fromAccountId;
    private String toAccountId;
    private String amount;
    private String fee;
    private String note;
    private String status;
    private LocalDateTime createdAt;
}