package com.jpmc.midascore.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "user_record")
public class UserRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", unique = true, nullable = false)
    private String accountId;

    @Column(name = "balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    // JPA requires a no-arg constructor
    public UserRecord() {}

    public UserRecord(String accountId, BigDecimal balance) {
        this.accountId = accountId;
        this.balance = balance == null ? BigDecimal.ZERO : balance;
    }

    public Long getId() {
        return id;
    }

    public String getAccountId() {
        return accountId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    /**
     * Subtract amount from balance (debit).
     * Assumes amount != null and amount >= 0 (caller should validate).
     */
    public void debit(BigDecimal amount) {
        if (amount == null) throw new IllegalArgumentException("amount required");
        this.balance = this.balance.subtract(amount);
    }

    /**
     * Add amount to balance (credit).
     */
    public void credit(BigDecimal amount) {
        if (amount == null) throw new IllegalArgumentException("amount required");
        this.balance = this.balance.add(amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserRecord)) return false;
        UserRecord that = (UserRecord) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(accountId, that.accountId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, accountId);
    }

    @Override
    public String toString() {
        return "UserRecord{" +
                "id=" + id +
                ", accountId='" + accountId + '\'' +
                ", balance=" + balance +
                '}';
    }
}
