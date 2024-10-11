package com.example.wallet.model;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import com.example.wallet.Exceptions.DepositAmountMustBePositiveException;
import com.example.wallet.Exceptions.InsufficientFundsException;
import com.example.wallet.Exceptions.WithdrawAmountMustBePositiveException;
import org.junit.jupiter.api.Test;

public class WalletTest {

    @Test
     void testWalletInitialization() {
        Wallet wallet = new Wallet();

        assertNotNull(wallet);
    }

    @Test
     void testDifferentWalletInstancesAreNotEqual() {
        Wallet wallet1 = new Wallet();
        Wallet wallet2 = new Wallet();

        assertNotEquals(wallet1, wallet2);
    }

    @Test
     void testDepositPositiveAmount() {
        Wallet wallet = new Wallet();
        BigDecimal expected = BigDecimal.valueOf(100);

        BigDecimal actual = wallet.deposit(BigDecimal.valueOf(100));

        assertEquals(expected, actual);
    }

    @Test
     void testDepositZeroAmount() {
        Wallet wallet = new Wallet();

        assertThrows(DepositAmountMustBePositiveException.class, () -> {
            wallet.deposit(BigDecimal.ZERO);
        });
    }

    @Test
     void testDepositNegativeAmount() {
        Wallet wallet = new Wallet();

        assertThrows(DepositAmountMustBePositiveException.class, () -> {
            wallet.deposit(BigDecimal.valueOf(-50));
        });
    }

    @Test
     void testWithdrawWithSufficientFunds() {
        Wallet wallet = new Wallet();
        wallet.deposit(BigDecimal.valueOf(100));
        BigDecimal expected = BigDecimal.valueOf(50);

        BigDecimal actual = wallet.withdraw(BigDecimal.valueOf(50));

        assertEquals(expected, actual);
    }

    @Test
     void testWithdrawWithInsufficientFunds() {
        Wallet wallet = new Wallet();

        assertThrows(InsufficientFundsException.class, () -> {
            wallet.withdraw(BigDecimal.valueOf(50));
        });
    }

    @Test
     void testWithdrawZeroAmount() {
        Wallet wallet = new Wallet();

        wallet.deposit(BigDecimal.valueOf(100));

        assertThrows(WithdrawAmountMustBePositiveException.class, () -> {
            wallet.withdraw(BigDecimal.ZERO);
        });
    }

    @Test
     void testWithdrawNegativeAmount() {
        Wallet wallet = new Wallet();

        wallet.deposit(BigDecimal.valueOf(100));

        assertThrows(WithdrawAmountMustBePositiveException.class, () -> {
            wallet.withdraw(BigDecimal.valueOf(-50));
        });
    }
}
