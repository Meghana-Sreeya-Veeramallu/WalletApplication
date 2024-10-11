package com.example.wallet.model;

import com.example.wallet.Exceptions.DepositAmountMustBePositiveException;
import com.example.wallet.Exceptions.InsufficientFundsException;
import com.example.wallet.Exceptions.WithdrawAmountMustBePositiveException;
import jakarta.persistence.*;

@Entity
@Table(name = "wallets")
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double balance;

    public Wallet() {
        this.balance = 0.0;
    }

    public Double deposit(Double amount) {
        if (amount.compareTo(0.0) <= 0) {
            throw new DepositAmountMustBePositiveException("Deposit amount must be positive");
        }
        this.balance += amount;
        return this.balance;
    }

    public Double withdraw(Double amount) {
        if (amount.compareTo(0.0) <= 0) {
            throw new WithdrawAmountMustBePositiveException("Withdrawal amount must be positive");
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }
        this.balance -= amount;
        return this.balance;
    }
}
