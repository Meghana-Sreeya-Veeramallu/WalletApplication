package com.example.wallet.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.wallet.Exceptions.*;
import com.example.wallet.model.Transaction;
import com.example.wallet.repository.TransactionRepository;
import com.example.wallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TransactionServiceTest {
    Long userId;
    Long walletId;

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private WalletRepository walletRepository;
    @Mock
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = 1L;
        walletId = 2L;
    }

    @Test
    void testGetTransactionHistoryWhenUserNotFound() {
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            transactionService.getTransactionHistory(userId);
        });

        verify(walletRepository, times(1)).findIdByUserId(userId);
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void testGetTransactionHistorySuccess() {
        Transaction transaction1 = new Transaction();
        Transaction transaction2 = new Transaction();

        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));
        when(transactionRepository.findByWalletId(walletId)).thenReturn(Arrays.asList(transaction1, transaction2));

        List<Transaction> transactions = transactionService.getTransactionHistory(userId);

        assertEquals(2, transactions.size());
        assertEquals(Arrays.asList(transaction1, transaction2), transactions);
        verify(walletRepository, times(1)).findIdByUserId(userId);
        verify(transactionRepository, times(1)).findByWalletId(walletId);
    }

    @Test
    void testGetTransactionHistoryWhenNoTransactions() {
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));
        when(transactionRepository.findByWalletId(walletId)).thenReturn(Collections.emptyList());

        List<Transaction> transactions = transactionService.getTransactionHistory(userId);

        assertEquals(0, transactions.size());
        verify(walletRepository).findIdByUserId(userId);
        verify(transactionRepository).findByWalletId(walletId);
    }
}
