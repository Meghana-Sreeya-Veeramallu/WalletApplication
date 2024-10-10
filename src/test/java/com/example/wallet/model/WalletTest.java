package com.example.wallet.model;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import com.example.wallet.Exceptions.DepositAmountMustBePositiveException;
import com.example.wallet.Exceptions.InsufficientFundsException;
import com.example.wallet.Exceptions.WithdrawAmountMustBePositiveException;
import org.junit.jupiter.api.Test;

public class WalletTest {

    @Test
    public void testWalletInitialization() {
        Wallet wallet = new Wallet();

        assertNotNull(wallet);
    }

    @Test
    public void testDifferentWalletInstancesAreNotEqual() {
        Wallet wallet1 = new Wallet();
        Wallet wallet2 = new Wallet();

        assertNotEquals(wallet1, wallet2);
    }

    @Test
    public void testWalletSetAndGetBalance() {
        Wallet wallet = new Wallet();

        assertDoesNotThrow(() -> wallet.deposit(BigDecimal.valueOf(100.00)));
    }

    @Test
    public void testDepositPositiveAmount() {
        Wallet wallet = new Wallet();

        assertDoesNotThrow(() -> wallet.deposit(BigDecimal.valueOf(100.00)));
    }

    @Test
    public void testDepositZeroAmount() {
        Wallet wallet = new Wallet();

        assertThrows(DepositAmountMustBePositiveException.class, () -> {
            wallet.deposit(BigDecimal.ZERO);
        });
    }

    @Test
    public void testDepositNegativeAmount() {
        Wallet wallet = new Wallet();

        assertThrows(DepositAmountMustBePositiveException.class, () -> {
            wallet.deposit(BigDecimal.valueOf(-50));
        });
    }

    @Test
    public void testWithdrawWithSufficientFunds() {
        Wallet wallet = new Wallet();

        wallet.deposit(BigDecimal.valueOf(100));

        assertDoesNotThrow(() -> wallet.withdraw(BigDecimal.valueOf(50)));
    }

    @Test
    public void testWithdrawWithInsufficientFunds() {
        Wallet wallet = new Wallet();

        assertThrows(InsufficientFundsException.class, () -> {
            wallet.withdraw(BigDecimal.valueOf(50));
        });
    }

    @Test
    public void testWithdrawZeroAmount() {
        Wallet wallet = new Wallet();

        wallet.deposit(BigDecimal.valueOf(100));

        assertThrows(WithdrawAmountMustBePositiveException.class, () -> {
            wallet.withdraw(BigDecimal.ZERO);
        });
    }

    @Test
    public void testWithdrawNegativeAmount() {
        Wallet wallet = new Wallet();

        wallet.deposit(BigDecimal.valueOf(100));

        assertThrows(WithdrawAmountMustBePositiveException.class, () -> {
            wallet.withdraw(BigDecimal.valueOf(-50));
        });
    }
}
