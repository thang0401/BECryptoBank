package com.cryptobank.backend.DTO.Web3Request;


public class DepositResponse {
    private String message;
    private String transactionHash;

    public DepositResponse(String message, String transactionHash) {
        this.message = message;
        this.transactionHash = transactionHash;
    }

    public String getMessage() { return message; }
    public String getTransactionHash() { return transactionHash; }
}