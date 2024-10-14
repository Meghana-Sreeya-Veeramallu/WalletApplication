package com.example.wallet.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.wallet.Exceptions.*;
import com.example.wallet.model.Wallet;
import com.example.wallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

public class WalletServiceTest {
    Long userId;
    Wallet wallet;

    @InjectMocks
    private WalletService walletService;

    @Mock
    private WalletRepository walletRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = 1L;
        wallet = new Wallet();
    }

    @Test
    void testDeposit() {
        Double depositAmount = 100.0;
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        Double newBalance = walletService.deposit(userId, depositAmount);

        assertEquals(depositAmount, newBalance);
        verify(walletRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testDepositNegativeAmount() {
        Double depositAmount = -100.0;
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        assertThrows(DepositAmountMustBePositiveException.class, () ->
                walletService.deposit(userId, depositAmount)
        );
        verify(walletRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testDepositWithInvalidUser() {
        Long invalidUserId = 2L;
        Double depositAmount = 100.0;
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
            walletService.deposit(invalidUserId, depositAmount)
        );
        verify(walletRepository, times(1)).findByUserId(invalidUserId);
    }

    @Test
    void testWithdrawWithSufficientFunds() {
        Double withdrawAmount = 50.0;
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        walletService.deposit(userId, 100.0);

        Double newBalance = walletService.withdraw(userId, withdrawAmount);

        assertEquals(50.0, newBalance);
        verify(walletRepository, times(2)).findByUserId(userId);
    }

    @Test
    void testWithdrawNegativeAmount() {
        Double withdrawAmount = -150.0;
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        walletService.deposit(userId, 100.0);

        assertThrows(WithdrawAmountMustBePositiveException.class, () ->
            walletService.withdraw(userId, withdrawAmount)
        );
        verify(walletRepository, times(2)).findByUserId(userId);
    }

    @Test
    void testWithdrawWithInsufficientFunds() {
        Double withdrawAmount = 150.0;
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        walletService.deposit(userId, 100.0);

        assertThrows(InsufficientFundsException.class, () ->
            walletService.withdraw(userId, withdrawAmount)
        );
        verify(walletRepository, times(2)).findByUserId(userId);
    }

    @Test
    void testWithdrawWithInvalidUser() {
        Long invalidUserId = 2L;
        Double withdrawAmount = 50.0;
        when(walletRepository.findByUserId(invalidUserId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
            walletService.withdraw(invalidUserId, withdrawAmount)
        );
        verify(walletRepository, times(1)).findByUserId(invalidUserId);
    }
}
