package com.jpmc.midascore;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String accountId;

    private String name;

    @Column(precision = 19, scale = 4)
    private BigDecimal balance = BigDecimal.ZERO;

    public User() {}

    public User(String accountId, String name, BigDecimal balance) {
        this.accountId = accountId;
        this.name = name;
        this.balance = balance;
    }

    public Long getId() { return id; }
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public void addToBalance(BigDecimal delta) {
        if (delta == null) return;
        this.balance = this.balance.add(delta);
    }
}