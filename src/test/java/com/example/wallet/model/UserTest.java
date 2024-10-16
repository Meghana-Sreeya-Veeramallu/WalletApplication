package com.example.wallet.model;

import static org.junit.jupiter.api.Assertions.*;

import com.example.wallet.Enums.CurrencyType;
import com.example.wallet.Exceptions.CurrencyCannotBeNullException;
import com.example.wallet.Exceptions.PasswordCannotBeNullOrEmptyException;
import com.example.wallet.Exceptions.UsernameCannotBeNullOrEmptyException;
import org.junit.jupiter.api.Test;

public class UserTest {

    @Test
    public void testValidUser() {
        User user = new User("testUser", "testPassword", CurrencyType.EUR);

        assertNotNull(user);
    }

    @Test
    public void testUsernameCannotBeNull() {
        assertThrows(UsernameCannotBeNullOrEmptyException.class, () -> {
            new User(null, "validPassword", CurrencyType.EUR);
        });
    }

    @Test
    public void testUsernameCannotBeEmpty() {
        assertThrows(UsernameCannotBeNullOrEmptyException.class, () -> {
            new User(" ", "validPassword", CurrencyType.EUR);
        });
    }

    @Test
    public void testPasswordCannotBeNull() {
        assertThrows(PasswordCannotBeNullOrEmptyException.class, () -> {
            new User("validUsername", null, CurrencyType.EUR);
        });
    }

    @Test
    public void testPasswordCannotBeEmpty() {
        assertThrows(PasswordCannotBeNullOrEmptyException.class, () -> {
            new User("validUsername", "", CurrencyType.EUR);
        });
    }

    @Test
    public void testValidUserWithCurrency() {
        User user = new User("testUser", "testPassword", CurrencyType.EUR);

        assertNotNull(user);
    }

    @Test
    public void testValidUserWithDefaultCurrency() {
        User user = new User("testUser", "testPassword", CurrencyType.INR);

        assertNotNull(user);
    }

    @Test
    public void testCurrencyCannotBeNull() {
        assertThrows(CurrencyCannotBeNullException.class, () -> {
            new User("testUser", "testPassword", null);
        });
    }
}
