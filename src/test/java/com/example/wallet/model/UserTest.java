package com.example.wallet.model;

import static org.junit.jupiter.api.Assertions.*;

import com.example.wallet.Exceptions.PasswordCannotBeNullOrEmptyException;
import com.example.wallet.Exceptions.UsernameCannotBeNullOrEmptyException;
import org.junit.jupiter.api.Test;

public class UserTest {

    @Test
    public void testValidUser() {
        User user = new User("testUser", "testPassword");

        assertNotNull(user);
    }

    @Test
    public void testUsernameCannotBeNull() {
        assertThrows(UsernameCannotBeNullOrEmptyException.class, () -> {
            new User(null, "validPassword");
        });
    }

    @Test
    public void testUsernameCannotBeEmpty() {
        assertThrows(UsernameCannotBeNullOrEmptyException.class, () -> {
            new User(" ", "validPassword");
        });
    }

    @Test
    public void testPasswordCannotBeNull() {
        assertThrows(PasswordCannotBeNullOrEmptyException.class, () -> {
            new User("validUsername", null);
        });
    }

    @Test
    public void testPasswordCannotBeEmpty() {
        assertThrows(PasswordCannotBeNullOrEmptyException.class, () -> {
            new User("validUsername", "");
        });
    }
}
