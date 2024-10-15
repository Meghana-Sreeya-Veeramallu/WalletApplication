package com.example.wallet.service;

import com.example.wallet.Enums.TransactionType;
import com.example.wallet.model.InterTransaction;
import com.example.wallet.model.IntraTransaction;
import com.example.wallet.model.Wallet;
import com.example.wallet.repository.InterTransactionRepository;
import com.example.wallet.repository.IntraTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class TransactionServiceTest {
    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private IntraTransactionRepository intraTransactionRepository;

    @Mock
    private InterTransactionRepository interTransactionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTransactionHistoryWithIntraTransactions() {
        Long walletId = 1L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));

        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(intraTransactions);
        when(interTransactionRepository.findBySenderWalletId(walletId)).thenReturn(new ArrayList<>());
        when(interTransactionRepository.findByRecipientWalletId(walletId)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactionHistory(walletId);

        assertEquals(1, result.size());
    }

    @Test
    void testGetTransactionHistoryWithInterTransactions() {
        Long walletId = 2L;

        List<InterTransaction> sentTransactions = new ArrayList<>();
        sentTransactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 100.0));

        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(new ArrayList<>());
        when(interTransactionRepository.findBySenderWalletId(walletId)).thenReturn(sentTransactions);
        when(interTransactionRepository.findByRecipientWalletId(walletId)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactionHistory(walletId);

        assertEquals(1, result.size());
    }

    @Test
    void testGetTransactionHistoryWithBothIntraAndInterTransactions() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.WITHDRAWAL, 10.0));

        List<InterTransaction> sentTransactions = new ArrayList<>();
        sentTransactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 100.0));

        List<InterTransaction> receivedTransactions = new ArrayList<>();
        receivedTransactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 150.0));

        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(intraTransactions);
        when(interTransactionRepository.findBySenderWalletId(walletId)).thenReturn(sentTransactions);
        when(interTransactionRepository.findByRecipientWalletId(walletId)).thenReturn(receivedTransactions);

        List<Object> result = transactionService.getTransactionHistory(walletId);

        assertEquals(4, result.size());
    }

    @Test
    void testGetTransactionHistory_WithNoTransactions() {
        Long walletId = 3L;

        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(new ArrayList<>());
        when(interTransactionRepository.findBySenderWalletId(walletId)).thenReturn(new ArrayList<>());
        when(interTransactionRepository.findByRecipientWalletId(walletId)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactionHistory(walletId);

        assertEquals(0, result.size());
        assertTrue(result.isEmpty());
    }
}
