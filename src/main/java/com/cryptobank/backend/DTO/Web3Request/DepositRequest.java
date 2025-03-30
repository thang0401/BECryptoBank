package com.cryptobank.backend.DTO.Web3Request;

public class DepositRequest {
    private String subWalletId;
    private String savingAccountId;
    private String amount;
    private String fromPubkey;
    private String toPubkey;

    public String getSubWalletId() { return subWalletId; }
    public void setSubWalletId(String subWalletId) { this.subWalletId = subWalletId; }

    public String getSavingAccountId() { return savingAccountId; }
    public void setSavingAccountId(String savingAccountId) { this.savingAccountId = savingAccountId; }

    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }

    public String getFromPubkey() { return fromPubkey; }
    public void setFromPubkey(String fromPubkey) { this.fromPubkey = fromPubkey; }

    public String getToPubkey() { return toPubkey; }
    public void setToPubkey(String toPubkey) { this.toPubkey = toPubkey; }
}