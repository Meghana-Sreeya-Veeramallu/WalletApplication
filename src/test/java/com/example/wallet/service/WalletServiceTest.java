package com.example.wallet.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.wallet.Exceptions.DepositAmountMustBePositiveException;
import com.example.wallet.Exceptions.InsufficientFundsException;
import com.example.wallet.Exceptions.UserNotFoundException;
import com.example.wallet.Exceptions.WithdrawAmountMustBePositiveException;
import com.example.wallet.model.User;
import com.example.wallet.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

public class WalletServiceTest {
    String username;
    String password;
    User user;

    @InjectMocks
    private WalletService walletService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        username = "testUser";
        password = "testPassword";
        user = new User(username, password);
    }

    @Test
    public void testDeposit() {
        BigDecimal depositAmount = BigDecimal.valueOf(100);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        BigDecimal newBalance = walletService.deposit(username, depositAmount);

        assertEquals(BigDecimal.valueOf(100), newBalance);
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void testDepositNegativeAmount() {
        BigDecimal depositAmount = BigDecimal.valueOf(-100);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        assertThrows(DepositAmountMustBePositiveException.class, () -> {
            walletService.deposit(username, depositAmount);
        });
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void testDepositWithInvalidUser() {
        String invalidUsername = "invalidUsername";
        BigDecimal depositAmount = BigDecimal.valueOf(100);
        when(userRepository.findByUsername(invalidUsername)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            walletService.deposit(invalidUsername, depositAmount);
        });
        verify(userRepository, times(1)).findByUsername(invalidUsername);
    }

    @Test
    public void testWithdrawWithSufficientFunds() {
        BigDecimal withdrawAmount = BigDecimal.valueOf(50);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        walletService.deposit(username, BigDecimal.valueOf(100));

        BigDecimal newBalance = walletService.withdraw(username, withdrawAmount);

        assertEquals(BigDecimal.valueOf(50), newBalance);
        verify(userRepository, times(2)).findByUsername(username);
    }

    @Test
    public void testWithdrawNegativeAmount() {
        BigDecimal withdrawAmount = BigDecimal.valueOf(-150);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        walletService.deposit(username, BigDecimal.valueOf(100));

        assertThrows(WithdrawAmountMustBePositiveException.class, () -> {
            walletService.withdraw(username, withdrawAmount);
        });
        verify(userRepository, times(2)).findByUsername(username);
    }

    @Test
    public void testWithdrawWithInsufficientFunds() {
        BigDecimal withdrawAmount = BigDecimal.valueOf(150);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        walletService.deposit(username, BigDecimal.valueOf(100));

        assertThrows(InsufficientFundsException.class, () -> {
            walletService.withdraw(username, withdrawAmount);
        });
        verify(userRepository, times(2)).findByUsername(username);
    }

    @Test
    public void testWithdrawWithInvalidUser() {
        String invalidUsername = "invalidUsername";
        BigDecimal withdrawAmount = BigDecimal.valueOf(50);
        when(userRepository.findByUsername(invalidUsername)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            walletService.withdraw(invalidUsername, withdrawAmount);
        });
        verify(userRepository, times(1)).findByUsername(invalidUsername);
    }
}
