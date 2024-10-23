package com.example.wallet.model;

import static org.junit.jupiter.api.Assertions.*;

import com.example.wallet.Enums.CurrencyType;
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

    @Test
    void testWalletInitializationWithCurrency() {
        Wallet wallet = new Wallet(CurrencyType.EUR);

        assertNotNull(wallet);
    }

    @Test
    void testDepositWithCurrency() {
        Wallet wallet = new Wallet(CurrencyType.JPY);
        Double balance = wallet.deposit(100.0);

        assertEquals(100.0, balance);
    }

    @Test
    void testDepositExceptionWithCurrency() {
        Wallet wallet = new Wallet(CurrencyType.JPY);

        assertThrows(DepositAmountMustBePositiveException.class, () -> {
            wallet.deposit(-50.0);
        });
    }

    @Test
    void testWithdrawWithCurrency() {
        Wallet wallet = new Wallet(CurrencyType.GBP);
        wallet.deposit(200.0);
        Double balance = wallet.withdraw(50.0);

        assertEquals(150.0, balance);
    }

    @Test
    void testWithdrawInsufficientFundsExceptionWithCurrency() {
        Wallet wallet = new Wallet(CurrencyType.GBP);
        wallet.deposit(200.0);

        assertThrows(InsufficientFundsException.class, () -> {
            wallet.withdraw(250.0);
        });
    }

    @Test
    void testWithdrawNegativeAmountExceptionWithCurrency() {
        Wallet wallet = new Wallet(CurrencyType.GBP);
        wallet.deposit(200.0);

        assertThrows(WithdrawAmountMustBePositiveException.class, () -> {
            wallet.withdraw(-50.0);
        });
    }
}
