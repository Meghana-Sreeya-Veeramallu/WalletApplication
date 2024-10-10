package com.example.wallet.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.wallet.Exceptions.UserNotFoundException;
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

    @InjectMocks
    private WalletService walletService;

    @Mock
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User("testUser", "testPass");
    }

    @Test
    public void testDeposit() {
        Long userId = 1L;
        BigDecimal depositAmount = BigDecimal.valueOf(100);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        BigDecimal newBalance = walletService.deposit(userId, depositAmount);

        assertEquals(BigDecimal.valueOf(100), newBalance);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testDepositWithInvalidUser() {
        Long invalidUserId = 999L;
        BigDecimal depositAmount = BigDecimal.valueOf(100);
        when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            walletService.deposit(invalidUserId, depositAmount);
        });
        verify(userRepository, times(1)).findById(invalidUserId);
    }

    @Test
    public void testWithdrawWithSufficientFunds() {
        Long userId = 1L;
        BigDecimal withdrawAmount = BigDecimal.valueOf(50);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        walletService.deposit(userId, BigDecimal.valueOf(100));

        BigDecimal newBalance = walletService.withdraw(userId, withdrawAmount);

        assertEquals(BigDecimal.valueOf(50), newBalance);
        verify(userRepository, times(2)).findById(userId);
    }

    @Test
    public void testWithdrawWithInsufficientFunds() {
        Long userId = 1L;
        BigDecimal withdrawAmount = BigDecimal.valueOf(150);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        walletService.deposit(userId, BigDecimal.valueOf(100));

        assertThrows(RuntimeException.class, () -> {
            walletService.withdraw(userId, withdrawAmount);
        });

        verify(userRepository, times(2)).findById(userId);
    }

    @Test
    public void testWithdrawWithInvalidUser() {
        Long invalidUserId = 999L;
        BigDecimal withdrawAmount = BigDecimal.valueOf(50);
        when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            walletService.withdraw(invalidUserId, withdrawAmount);
        });
        verify(userRepository, times(1)).findById(invalidUserId);
    }
}
