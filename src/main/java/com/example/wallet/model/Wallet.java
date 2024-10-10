package com.example.wallet.model;

import com.example.wallet.Exceptions.BalanceCannotBeNegativeException;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal balance;

    @OneToOne
    @MapsId
    private User user;

    public Wallet() {
        this.balance = BigDecimal.ZERO;
    }

    public void setBalance(BigDecimal balance) {
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BalanceCannotBeNegativeException("Balance cannot be negative");
        }
        this.balance = balance;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
