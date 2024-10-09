package com.example.wallet.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class UserTest {

    @Test
    public void testUserInitialization() {
        User user = new User();

        assertNotNull(user);
    }

    @Test
    public void testDifferentUserInstancesAreNotEqual() {
        User user1 = new User();
        User user2 = new User();

        assertNotEquals(user1, user2);
    }

    @Test
    public void testGetAndSetUsername() {
        User user = new User();

        user.setUsername("testUser");

        assertEquals("testUser", user.getUsername());
    }

    @Test
    public void testGetAndSetPassword() {
        User user = new User();

        user.setPassword("testPassword");

        assertEquals("testPassword", user.getPassword());
    }

    @Test
    public void testGetAndSetId() {
        User user = new User();

        user.setId(1L);

        assertNotNull(user.getId());
        assertEquals(1L, user.getId());
    }

    @Test
    public void testWalletRelationship() {
        User user = new User();
        Wallet wallet = new Wallet();

        user.setWallet(wallet);

        assertEquals(wallet, user.getWallet());
    }

    @Test
    public void testInvalidWalletRelationship() {
        User user1 = new User();
        Wallet wallet1 = new Wallet();
        User user2 = new User();
        Wallet wallet2 = new Wallet();

        user1.setWallet(wallet1);
        user2.setWallet(wallet2);

        assertNotEquals(user1, user2);
        assertNotEquals(wallet1, wallet2);
        assertNotEquals(user1, wallet2.getUser());
        assertNotEquals(user2, wallet1.getUser());
        assertNotEquals(wallet1, user2.getWallet());
        assertNotEquals(wallet2, user1.getWallet());
    }
}
