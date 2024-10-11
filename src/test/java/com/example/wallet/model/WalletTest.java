package com.example.wallet.model;

import static org.junit.jupiter.api.Assertions.*;

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
        Double expected = 100.0;

        Double actual = wallet.deposit(100.0);

        assertEquals(expected, actual);
    }

    @Test
     void testDepositZeroAmount() {
        Wallet wallet = new Wallet();

        assertThrows(DepositAmountMustBePositiveException.class, () -> {
            wallet.deposit(0.0);
        });
    }

    @Test
     void testDepositNegativeAmount() {
        Wallet wallet = new Wallet();

        assertThrows(DepositAmountMustBePositiveException.class, () -> {
            wallet.deposit(-50.0);
        });
    }

    @Test
     void testWithdrawWithSufficientFunds() {
        Wallet wallet = new Wallet();
        wallet.deposit(100.0);
        Double expected = 50.0;

        Double actual = wallet.withdraw(50.0);

        assertEquals(expected, actual);
    }

    @Test
     void testWithdrawWithInsufficientFunds() {
        Wallet wallet = new Wallet();

        assertThrows(InsufficientFundsException.class, () -> {
            wallet.withdraw(50.0);
        });
    }

    @Test
     void testWithdrawZeroAmount() {
        Wallet wallet = new Wallet();

        wallet.deposit(100.0);

        assertThrows(WithdrawAmountMustBePositiveException.class, () -> {
            wallet.withdraw(0.0);
        });
    }

    @Test
     void testWithdrawNegativeAmount() {
        Wallet wallet = new Wallet();

        wallet.deposit(100.0);

        assertThrows(WithdrawAmountMustBePositiveException.class, () -> {
            wallet.withdraw(-50.0);
        });
    }
}
