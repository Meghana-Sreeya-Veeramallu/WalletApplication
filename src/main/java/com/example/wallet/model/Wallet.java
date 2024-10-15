package com.example.wallet.model;

import com.example.wallet.Enums.CurrencyType;
import com.example.wallet.Exceptions.DepositAmountMustBePositiveException;
import com.example.wallet.Exceptions.InsufficientFundsException;
import com.example.wallet.Exceptions.TransferAmountMustBePositiveException;
import com.example.wallet.Exceptions.WithdrawAmountMustBePositiveException;
import jakarta.persistence.*;

@Entity
@Table(name = "wallets")
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double balance;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

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

    public Double transfer(Wallet recipientWallet, Double amount) {
        if (amount <= 0) {
            throw new TransferAmountMustBePositiveException("Transfer amount must be positive");
        }
        if (this.balance < amount) {
            throw new InsufficientFundsException("Insufficient funds for transfer");
        }

        double amountInRecipientCurrency = getAmountInRecipientCurrency(recipientWallet, amount);

        this.balance -= amount;
        recipientWallet.balance += amountInRecipientCurrency;

        return this.balance;
    }

    private double getAmountInRecipientCurrency(Wallet recipientWallet, Double amount) {
        double amountInBaseCurrency = this.currency.toBaseCurrency(amount);
        return recipientWallet.currency.fromBaseCurrency(amountInBaseCurrency);
    }
}
