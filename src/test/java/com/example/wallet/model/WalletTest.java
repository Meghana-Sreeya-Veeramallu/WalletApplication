package com.example.wallet.model;

import static org.junit.jupiter.api.Assertions.*;

import com.example.wallet.Enums.CurrencyType;
import com.example.wallet.Exceptions.DepositAmountMustBePositiveException;
import com.example.wallet.Exceptions.InsufficientFundsException;
import com.example.wallet.Exceptions.TransferAmountMustBePositiveException;
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
    void testSuccessfulTransfer() {
        Wallet senderWallet = new Wallet();
        Wallet recipientWallet = new Wallet();
        Double amount = 30.0;

        senderWallet.deposit(100.0);
        Double newBalance = senderWallet.transfer(recipientWallet, amount);

        assertEquals(70.0, newBalance);
    }

    @Test
    void testTransferWhenAmountIsNegative() {
        Wallet senderWallet = new Wallet();
        Wallet recipientWallet = new Wallet();
        Double amount = -10.0;

        senderWallet.deposit(100.0);

        Exception exception = assertThrows(TransferAmountMustBePositiveException.class, () -> {
            senderWallet.transfer(recipientWallet, amount);
        });
        assertEquals("Transfer amount must be positive", exception.getMessage());
    }

    @Test
    void testTransferWhenInsufficientFunds() {
        Wallet senderWallet = new Wallet();
        Wallet recipientWallet = new Wallet();
        Double amount = 150.0;

        senderWallet.deposit(100.0);

        Exception exception = assertThrows(InsufficientFundsException.class, () -> {
            senderWallet.transfer(recipientWallet, amount);
        });
        assertEquals("Insufficient funds for transfer", exception.getMessage());
    }

    @Test
    void testTransferZeroAmount() {
        Wallet senderWallet = new Wallet();
        Wallet recipientWallet = new Wallet();
        Double amount = 0.0;

        senderWallet.deposit(100.0);

        Exception exception = assertThrows(TransferAmountMustBePositiveException.class, () -> {
            senderWallet.transfer(recipientWallet, amount);
        });
        assertEquals("Transfer amount must be positive", exception.getMessage());
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

    @Test
    void testTransferFromUSDtoEUR() {
        Wallet senderWallet = new Wallet(CurrencyType.USD);
        Wallet recipientWallet = new Wallet(CurrencyType.EUR);
        senderWallet.deposit(100.0);
        Double amountToTransfer = 100.0;
        Double expectedSenderBalance = 0.0;

        Double newSenderBalance = senderWallet.transfer(recipientWallet, amountToTransfer);

        assertEquals(expectedSenderBalance, newSenderBalance);
    }

    @Test
    void testTransferFromEURtoJPY() {
        Wallet senderWallet = new Wallet(CurrencyType.EUR);
        Wallet recipientWallet = new Wallet(CurrencyType.JPY);
        senderWallet.deposit(200.0);
        Double amountToTransfer = 100.0;
        Double expectedSenderBalance = 100.0;

        Double newSenderBalance = senderWallet.transfer(recipientWallet, amountToTransfer);

        assertEquals(expectedSenderBalance, newSenderBalance);
    }

    @Test
    void testTransferInsufficientFundsExceptionFromJPYtoINR() {
        Wallet senderWallet = new Wallet(CurrencyType.JPY);
        Wallet recipientWallet = new Wallet(CurrencyType.INR);
        senderWallet.deposit(5000.0);
        Double amountToTransfer = 10000.0;

        assertThrows(InsufficientFundsException.class, () -> {
            senderWallet.transfer(recipientWallet, amountToTransfer);
        });
    }

    @Test
    void testTransferBetweenGBPtoUSD() {
        Wallet senderWallet = new Wallet(CurrencyType.GBP);
        Wallet recipientWallet = new Wallet(CurrencyType.USD);
        senderWallet.deposit(150.0);
        Double amountToTransfer = -75.0;

        assertThrows(TransferAmountMustBePositiveException.class, () -> {
            senderWallet.transfer(recipientWallet, amountToTransfer);
        });
    }
}
