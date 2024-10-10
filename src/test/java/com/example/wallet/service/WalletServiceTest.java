package com.example.wallet.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.wallet.Exceptions.DepositAmountMustBePositiveException;
import com.example.wallet.Exceptions.InsufficientFundsException;
import com.example.wallet.Exceptions.UserNotFoundException;
import com.example.wallet.Exceptions.WithdrawAmountMustBePositiveException;
import com.example.wallet.model.Wallet;
import com.example.wallet.repository.WalletRepository;
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
    private WalletRepository walletRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testDeposit() {
        Long userId = 1L;
        BigDecimal depositAmount = BigDecimal.valueOf(100);
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(new Wallet()));

        BigDecimal newBalance = walletService.deposit(userId, depositAmount);

        assertEquals(BigDecimal.valueOf(100), newBalance);
        verify(walletRepository, times(1)).findByUserId(userId);
    }

    @Test
    public void testDepositNegativeAmount() {
        Long userId = 1L;
        BigDecimal depositAmount = BigDecimal.valueOf(-100);
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(new Wallet()));

        assertThrows(DepositAmountMustBePositiveException.class, () -> {
            walletService.deposit(userId, depositAmount);
        });
        verify(walletRepository, times(1)).findByUserId(userId);
    }

    @Test
    public void testDepositWithInvalidUser() {
        Long invalidUserId = 999L;
        BigDecimal depositAmount = BigDecimal.valueOf(100);
        when(walletRepository.findByUserId(invalidUserId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            walletService.deposit(invalidUserId, depositAmount);
        });
        verify(walletRepository, times(1)).findByUserId(invalidUserId);
    }

    @Test
    public void testWithdrawWithSufficientFunds() {
        Long userId = 1L;
        BigDecimal withdrawAmount = BigDecimal.valueOf(50);
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(new Wallet()));
        walletService.deposit(userId, BigDecimal.valueOf(100));

        BigDecimal newBalance = walletService.withdraw(userId, withdrawAmount);

        assertEquals(BigDecimal.valueOf(50), newBalance);
        verify(walletRepository, times(2)).findByUserId(userId);
    }

    @Test
    public void testWithdrawNegativeAmount() {
        Long userId = 1L;
        BigDecimal withdrawAmount = BigDecimal.valueOf(-150);
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(new Wallet()));
        walletService.deposit(userId, BigDecimal.valueOf(100));

        assertThrows(WithdrawAmountMustBePositiveException.class, () -> {
            walletService.withdraw(userId, withdrawAmount);
        });

        verify(walletRepository, times(2)).findByUserId(userId);
    }

    @Test
    public void testWithdrawWithInsufficientFunds() {
        Long userId = 1L;
        BigDecimal withdrawAmount = BigDecimal.valueOf(150);
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(new Wallet()));
        walletService.deposit(userId, BigDecimal.valueOf(100));

        assertThrows(InsufficientFundsException.class, () -> {
            walletService.withdraw(userId, withdrawAmount);
        });

        verify(walletRepository, times(2)).findByUserId(userId);
    }

    @Test
    public void testWithdrawWithInvalidUser() {
        Long invalidUserId = 999L;
        BigDecimal withdrawAmount = BigDecimal.valueOf(50);
        when(walletRepository.findByUserId(invalidUserId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            walletService.withdraw(invalidUserId, withdrawAmount);
        });
        verify(walletRepository, times(1)).findByUserId(invalidUserId);
    }
}
