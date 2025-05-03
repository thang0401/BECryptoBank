package com.cryptobank.backend.DTO.DebitTransaction;

import com.cryptobank.backend.entity.DebitTransaction;

public class DepositResponse {
    private DebitDTO transaction;
    private DebitDTO user;

    public DepositResponse() {}

    public DepositResponse(DebitDTO transaction, DebitDTO user) {
        this.transaction = transaction;
        this.user = user;
    }

    // Getters and setters
    public DebitDTO getTransaction() { return transaction; }
    public void setTransaction(DebitDTO transaction) { this.transaction = transaction; }
    public DebitDTO getUser() { return user; }
    public void setUser(DebitDTO user) { this.user = user; }
}