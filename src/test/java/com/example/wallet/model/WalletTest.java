package com.example.wallet.model;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import com.example.wallet.Exceptions.BalanceCannotBeNegativeException;
import org.junit.jupiter.api.Test;

public class WalletTest {

    @Test
    public void testWalletInitialization() {
        Wallet wallet = new Wallet();

        assertNotNull(wallet);
    }

    @Test
    public void testWalletInitialBalance() {
        Wallet wallet = new Wallet();

        assertEquals(BigDecimal.ZERO, wallet.getBalance());
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

        wallet.setBalance(BigDecimal.valueOf(100.00));

        assertEquals(BigDecimal.valueOf(100.00), wallet.getBalance());
    }

    @Test
    public void testSetNegativeBalance() {
        Wallet wallet = new Wallet();

        assertThrows(BalanceCannotBeNegativeException.class, () -> wallet.setBalance(BigDecimal.valueOf(-100.00)));
    }
}
