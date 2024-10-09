package com.example.wallet.model;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

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

        wallet.setBalance(BigDecimal.valueOf(100.00));

        assertEquals(BigDecimal.valueOf(100.00), wallet.getBalance());
    }

    @Test
    public void testSetNegativeBalance() {
        Wallet wallet = new Wallet();

        assertThrows(IllegalArgumentException.class, () -> wallet.setBalance(BigDecimal.valueOf(-100.00)));
    }

    @Test
    public void testUserRelationship() {
        Wallet wallet = new Wallet();
        User user = new User();

        wallet.setUser(user);

        assertEquals(user, wallet.getUser());
    }

    @Test
    public void testInvalidUserRelationship() {
        User user1 = new User();
        Wallet wallet1 = new Wallet();
        User user2 = new User();
        Wallet wallet2 = new Wallet();

        wallet1.setUser(user1);
        wallet2.setUser(user2);

        assertNotEquals(user1, user2);
        assertNotEquals(wallet1, wallet2);
        assertNotEquals(user1, wallet2.getUser());
        assertNotEquals(user2, wallet1.getUser());
        assertNotEquals(wallet1, user2.getWallet());
        assertNotEquals(wallet2, user1.getWallet());
    }
}
