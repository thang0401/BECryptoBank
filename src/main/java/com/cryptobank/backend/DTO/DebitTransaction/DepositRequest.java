package com.cryptobank.backend.DTO.DebitTransaction;


public class DepositRequest {
    private String amount;
    private String debitAccountId; // Đổi từ debitWalletId thành debitAccountId
    private String fromPubKey;
    private String toPubKey;
    private String transactionHash;

    // Getters and setters
    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }
    public String getDebitAccountId() { return debitAccountId; }
    public void setDebitAccountId(String debitAccountId) { this.debitAccountId = debitAccountId; }
    public String getFromPubKey() { return fromPubKey; }
    public void setFromPubKey(String fromPubKey) { this.fromPubKey = fromPubKey; }
    public String getToPubKey() { return toPubKey; }
    public void setToPubKey(String toPubKey) { this.toPubKey = toPubKey; }
    public String getTransactionHash() { return transactionHash; }
    public void setTransactionHash(String transactionHash) { this.transactionHash = transactionHash; }
}