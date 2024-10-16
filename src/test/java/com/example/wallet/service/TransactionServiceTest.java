package com.example.wallet.service;

import com.example.wallet.Enums.SortOrder;
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
import static org.mockito.Mockito.*;

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

        when(intraTransactionRepository.findByWalletIdAndTransactionType(walletId, null)).thenReturn(intraTransactions);
        when(interTransactionRepository.findBySenderWalletIdAndTransactionType(walletId, null)).thenReturn(new ArrayList<>());
        when(interTransactionRepository.findByRecipientWalletIdAndTransactionType(walletId, null)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactionHistory(walletId, null, null);

        assertEquals(1, result.size());
    }

    @Test
    void testGetTransactionHistoryWithInterTransactions() {
        Long walletId = 2L;

        List<InterTransaction> sentTransactions = new ArrayList<>();
        sentTransactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 100.0));

        when(intraTransactionRepository.findByWalletIdAndTransactionType(walletId, null)).thenReturn(new ArrayList<>());
        when(interTransactionRepository.findBySenderWalletIdAndTransactionType(walletId, null)).thenReturn(sentTransactions);
        when(interTransactionRepository.findByRecipientWalletIdAndTransactionType(walletId, null)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactionHistory(walletId, null, null);

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

        when(intraTransactionRepository.findByWalletIdAndTransactionType(walletId, null)).thenReturn(intraTransactions);
        when(interTransactionRepository.findBySenderWalletIdAndTransactionType(walletId, null)).thenReturn(sentTransactions);
        when(interTransactionRepository.findByRecipientWalletIdAndTransactionType(walletId, null)).thenReturn(receivedTransactions);

        List<Object> result = transactionService.getTransactionHistory(walletId, null, null);

        assertEquals(4, result.size());
    }

    @Test
    void testGetTransactionHistory_WithNoTransactions() {
        Long walletId = 3L;

        when(intraTransactionRepository.findByWalletIdAndTransactionType(walletId, null)).thenReturn(new ArrayList<>());
        when(interTransactionRepository.findBySenderWalletIdAndTransactionType(walletId, null)).thenReturn(new ArrayList<>());
        when(interTransactionRepository.findByRecipientWalletIdAndTransactionType(walletId, null)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactionHistory(walletId, null, null);

        assertEquals(0, result.size());
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetTransactionHistoryWithSortAscending() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.WITHDRAWAL, 10.0));

        List<InterTransaction> sentTransactions = new ArrayList<>();
        sentTransactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 100.0));

        List<InterTransaction> receivedTransactions = new ArrayList<>();
        receivedTransactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 150.0));

        when(intraTransactionRepository.findByWalletIdAndTransactionType(walletId, null)).thenReturn(intraTransactions);
        when(interTransactionRepository.findBySenderWalletIdAndTransactionType(walletId, null)).thenReturn(sentTransactions);
        when(interTransactionRepository.findByRecipientWalletIdAndTransactionType(walletId, null)).thenReturn(receivedTransactions);

        List<Object> result = transactionService.getTransactionHistory(walletId, SortOrder.ASC, null);

        assertEquals(4, result.size());
    }

    @Test
    void testGetTransactionHistoryWithSortDescending() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.WITHDRAWAL, 10.0));

        List<InterTransaction> sentTransactions = new ArrayList<>();
        sentTransactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 100.0));

        List<InterTransaction> receivedTransactions = new ArrayList<>();
        receivedTransactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 150.0));

        when(intraTransactionRepository.findByWalletIdAndTransactionType(walletId, null)).thenReturn(intraTransactions);
        when(interTransactionRepository.findBySenderWalletIdAndTransactionType(walletId, null)).thenReturn(sentTransactions);
        when(interTransactionRepository.findByRecipientWalletIdAndTransactionType(walletId, null)).thenReturn(receivedTransactions);

        List<Object> result = transactionService.getTransactionHistory(walletId, SortOrder.DESC, null);

        assertEquals(4, result.size());
    }

    @Test
    void testGetTransactionHistoryWithTypeDeposit() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));

        when(intraTransactionRepository.findByWalletIdAndTransactionType(walletId, TransactionType.DEPOSIT)).thenReturn(intraTransactions);
        when(interTransactionRepository.findBySenderWalletIdAndTransactionType(walletId, TransactionType.DEPOSIT)).thenReturn(new ArrayList<>());
        when(interTransactionRepository.findByRecipientWalletIdAndTransactionType(walletId, TransactionType.DEPOSIT)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactionHistory(walletId, null, TransactionType.DEPOSIT);

        assertEquals(1, result.size());
        verify(intraTransactionRepository, times(1)).findByWalletIdAndTransactionType(walletId, TransactionType.DEPOSIT);
        verify(interTransactionRepository, times(1)).findBySenderWalletIdAndTransactionType(walletId, TransactionType.DEPOSIT);
        verify(interTransactionRepository, times(1)).findByRecipientWalletIdAndTransactionType(walletId, TransactionType.DEPOSIT);
    }

    @Test
    void testGetTransactionHistoryWithTypeWithdrawal() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.WITHDRAWAL, 100.0));

        when(intraTransactionRepository.findByWalletIdAndTransactionType(walletId, TransactionType.WITHDRAWAL)).thenReturn(intraTransactions);
        when(interTransactionRepository.findBySenderWalletIdAndTransactionType(walletId, TransactionType.WITHDRAWAL)).thenReturn(new ArrayList<>());
        when(interTransactionRepository.findByRecipientWalletIdAndTransactionType(walletId, TransactionType.WITHDRAWAL)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactionHistory(walletId, null, TransactionType.WITHDRAWAL);

        assertEquals(1, result.size());
        verify(intraTransactionRepository, times(1)).findByWalletIdAndTransactionType(walletId, TransactionType.WITHDRAWAL);
        verify(interTransactionRepository, times(1)).findBySenderWalletIdAndTransactionType(walletId, TransactionType.WITHDRAWAL);
        verify(interTransactionRepository, times(1)).findByRecipientWalletIdAndTransactionType(walletId, TransactionType.WITHDRAWAL);
    }

    @Test
    void testGetTransactionHistoryWithTypeTransfer() {
        Long walletId = 2L;

        List<InterTransaction> interTransactions = new ArrayList<>();
        interTransactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 100.0));

        when(intraTransactionRepository.findByWalletIdAndTransactionType(walletId, TransactionType.TRANSFER)).thenReturn(new ArrayList<>());
        when(interTransactionRepository.findBySenderWalletIdAndTransactionType(walletId, TransactionType.TRANSFER)).thenReturn(interTransactions);
        when(interTransactionRepository.findByRecipientWalletIdAndTransactionType(walletId, TransactionType.TRANSFER)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactionHistory(walletId, null, TransactionType.TRANSFER);

        assertEquals(1, result.size());
        verify(intraTransactionRepository, times(1)).findByWalletIdAndTransactionType(walletId, TransactionType.TRANSFER);
        verify(interTransactionRepository, times(1)).findBySenderWalletIdAndTransactionType(walletId, TransactionType.TRANSFER);
        verify(interTransactionRepository, times(1)).findByRecipientWalletIdAndTransactionType(walletId, TransactionType.TRANSFER);
    }

    @Test
    void testGetTransactionHistoryWithSortAscendingAndTypeTransfer() {
        Long walletId = 2L;

        List<InterTransaction> interTransactions = new ArrayList<>();
        interTransactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 100.0));
        interTransactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 200.0));

        when(intraTransactionRepository.findByWalletIdAndTransactionType(walletId, TransactionType.TRANSFER)).thenReturn(new ArrayList<>());
        when(interTransactionRepository.findBySenderWalletIdAndTransactionType(walletId, TransactionType.TRANSFER)).thenReturn(interTransactions);
        when(interTransactionRepository.findByRecipientWalletIdAndTransactionType(walletId, TransactionType.TRANSFER)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactionHistory(walletId, SortOrder.ASC, TransactionType.TRANSFER);

        assertEquals(2, result.size());
        verify(intraTransactionRepository, times(1)).findByWalletIdAndTransactionType(walletId, TransactionType.TRANSFER);
        verify(interTransactionRepository, times(1)).findBySenderWalletIdAndTransactionType(walletId, TransactionType.TRANSFER);
        verify(interTransactionRepository, times(1)).findByRecipientWalletIdAndTransactionType(walletId, TransactionType.TRANSFER);
    }

    @Test
    void testGetTransactionHistoryWithSortDescendingAndTypeDeposit() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 200.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));

        when(intraTransactionRepository.findByWalletIdAndTransactionType(walletId, TransactionType.DEPOSIT)).thenReturn(intraTransactions);
        when(interTransactionRepository.findBySenderWalletIdAndTransactionType(walletId, TransactionType.DEPOSIT)).thenReturn(new ArrayList<>());
        when(interTransactionRepository.findByRecipientWalletIdAndTransactionType(walletId, TransactionType.DEPOSIT)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactionHistory(walletId, SortOrder.DESC, TransactionType.DEPOSIT);

        assertEquals(2, result.size());
        verify(intraTransactionRepository, times(1)).findByWalletIdAndTransactionType(walletId, TransactionType.DEPOSIT);
        verify(interTransactionRepository, times(1)).findBySenderWalletIdAndTransactionType(walletId, TransactionType.DEPOSIT);
        verify(interTransactionRepository, times(1)).findByRecipientWalletIdAndTransactionType(walletId, TransactionType.DEPOSIT);
    }
}
