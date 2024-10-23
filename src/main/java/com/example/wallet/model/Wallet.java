package com.example.wallet.model;

import com.example.wallet.Enums.CurrencyType;
import com.example.wallet.Exceptions.DepositAmountMustBePositiveException;
import com.example.wallet.Exceptions.InsufficientFundsException;
import com.example.wallet.Exceptions.WithdrawAmountMustBePositiveException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "wallets")
public class Wallet {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Getter
    private Double balance;

    @Setter
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Getter
    @Enumerated(EnumType.STRING)
    private CurrencyType currency;

    public Wallet() {
        this.balance = 0.0;
        this.currency = CurrencyType.INR;
    }

    public Wallet(CurrencyType currency) {
        this.balance = 0.0;
        this.currency = currency;
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
