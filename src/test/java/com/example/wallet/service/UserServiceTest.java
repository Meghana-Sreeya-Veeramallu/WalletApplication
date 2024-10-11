package com.example.wallet.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.wallet.Exceptions.*;
import com.example.wallet.model.User;
import com.example.wallet.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

public class UserServiceTest {
    String username;
    String password;
    User user;

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
     void setUp() {
        MockitoAnnotations.openMocks(this);
        username = "testUser";
        password = "testPassword";
        user = new User(username, password);
    }

    @Test
     void testRegisterUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);
        String password = "testPassword";
        String username = "testUser";

        User registeredUser = userService.registerUser(username, password);

        assertNotNull(registeredUser);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
     void testRegisterInvalidUser() {
        String password = "";
        String username = "testUser";

        assertThrows(PasswordCannotBeNullOrEmptyException.class , () ->userService.registerUser(username, password));
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    void testDeposit() {
        Double depositAmount = 100.0;
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        Double newBalance = userService.deposit(username, depositAmount);

        assertEquals(depositAmount, newBalance);
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testDepositNegativeAmount() {
        Double depositAmount = -100.0;
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        assertThrows(DepositAmountMustBePositiveException.class, () -> {
            userService.deposit(username, depositAmount);
        });
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testDepositWithInvalidUser() {
        String invalidUsername = "invalidUsername";
        Double depositAmount = 100.0;
        when(userRepository.findByUsername(invalidUsername)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.deposit(invalidUsername, depositAmount);
        });
        verify(userRepository, times(1)).findByUsername(invalidUsername);
    }

    @Test
    void testWithdrawWithSufficientFunds() {
        Double withdrawAmount = 50.0;
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        userService.deposit(username, 100.0);

        Double newBalance = userService.withdraw(username, withdrawAmount);

        assertEquals(50.0, newBalance);
        verify(userRepository, times(2)).findByUsername(username);
    }

    @Test
    void testWithdrawNegativeAmount() {
        Double withdrawAmount = -150.0;
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        userService.deposit(username, 100.0);

        assertThrows(WithdrawAmountMustBePositiveException.class, () -> {
            userService.withdraw(username, withdrawAmount);
        });
        verify(userRepository, times(2)).findByUsername(username);
    }

    @Test
    void testWithdrawWithInsufficientFunds() {
        Double withdrawAmount = 150.0;
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        userService.deposit(username, 100.0);

        assertThrows(InsufficientFundsException.class, () -> {
            userService.withdraw(username, withdrawAmount);
        });
        verify(userRepository, times(2)).findByUsername(username);
    }

    @Test
    void testWithdrawWithInvalidUser() {
        String invalidUsername = "invalidUsername";
        Double withdrawAmount = 50.0;
        when(userRepository.findByUsername(invalidUsername)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.withdraw(invalidUsername, withdrawAmount);
        });
        verify(userRepository, times(1)).findByUsername(invalidUsername);
    }
}
