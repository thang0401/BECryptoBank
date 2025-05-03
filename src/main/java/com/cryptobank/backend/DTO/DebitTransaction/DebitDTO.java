package com.cryptobank.backend.DTO.DebitTransaction;

import com.cryptobank.backend.entity.DebitTransaction;
import com.cryptobank.backend.entity.User;

public class DebitDTO {
    private String id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String debitAccountId; // Đổi từ debitWalletId thành debitAccountId
    private String amount;
    private String transactionType;
    private String transactionHash;
    private String fromPubKey;
    private String toPubKey;
    private String status;
    private String timestamp;

    public DebitDTO() {}

    public DebitDTO(User user) {
        this.id = user.getId();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.debitAccountId  = user.getDebitWalletList().getId(); // Đổi từ debitWalletList thành debitAccount
    }

    public DebitDTO(DebitTransaction transaction) {
        this.id = transaction.getId();
        this.amount = transaction.getAmount().toString();
        this.transactionType = transaction.getTransactionType();
        this.transactionHash = transaction.getTransactionHash();
        this.fromPubKey = transaction.getFromPubKey();
        this.toPubKey = transaction.getToPubKey();
        this.status = transaction.getStatus().getName();
        this.timestamp = transaction.getCreatedAt().toString();
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getDebitAccountId() { return debitAccountId; }
    public void setDebitAccountId(String debitAccountId) { this.debitAccountId = debitAccountId; }
    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }
    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    public String getTransactionHash() { return transactionHash; }
    public void setTransactionHash(String transactionHash) { this.transactionHash = transactionHash; }
    public String getFromPubKey() { return fromPubKey; }
    public void setFromPubKey(String fromPubKey) { this.fromPubKey = fromPubKey; }
    public String getToPubKey() { return toPubKey; }
    public void setToPubKey(String toPubKey) { this.toPubKey = toPubKey; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}