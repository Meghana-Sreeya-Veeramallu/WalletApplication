package com.example.wallet.model;

import com.example.wallet.Exceptions.DepositAmountMustBePositiveException;
import com.example.wallet.Exceptions.InsufficientFundsException;
import com.example.wallet.Exceptions.WithdrawAmountMustBePositiveException;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "wallets")
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal balance;

    @OneToOne(cascade = CascadeType.ALL)
    private User user;

    public Wallet() {
        this.balance = BigDecimal.ZERO;
    }

    public BigDecimal deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DepositAmountMustBePositiveException("Deposit amount must be positive");
        }
        this.balance = this.balance.add(amount);
        return this.balance;
    }

    public BigDecimal withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new WithdrawAmountMustBePositiveException("Withdrawal amount must be positive");
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }
        this.balance = this.balance.subtract(amount);
        return this.balance;
    }
}
