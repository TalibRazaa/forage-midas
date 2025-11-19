package com.jpmc.midascore;

import java.math.BigDecimal;

public class TransactionDTO {
    private String id;
    private BigDecimal amount;
    private String currency;
    private String accountId;
    private String timestamp;

    public TransactionDTO() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}