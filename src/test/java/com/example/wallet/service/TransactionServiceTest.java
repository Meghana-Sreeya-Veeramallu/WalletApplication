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
        when(interTransactionRepository.findByWalletIdAndTransactionType(walletId, null)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactionHistory(walletId, null, null, null);

        assertEquals(1, result.size());
    }

    @Test
    void testGetTransactionHistoryWithInterTransactions() {
        Long walletId = 2L;

        List<InterTransaction> interTransactions = new ArrayList<>();
        interTransactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 100.0));

        when(intraTransactionRepository.findByWalletIdAndTransactionType(walletId, null)).thenReturn(new ArrayList<>());
        when(interTransactionRepository.findByWalletIdAndTransactionType(walletId, null)).thenReturn(interTransactions);

        List<Object> result = transactionService.getTransactionHistory(walletId, null, null, null);

        assertEquals(1, result.size());
    }

    @Test
    void testGetTransactionHistoryWithBothIntraAndInterTransactions() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.WITHDRAWAL, 10.0));

        List<InterTransaction> interTransactions = new ArrayList<>();
        interTransactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 100.0));
        interTransactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 150.0));

        when(intraTransactionRepository.findByWalletIdAndTransactionType(walletId, null)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletIdAndTransactionType(walletId, null)).thenReturn(interTransactions);

        List<Object> result = transactionService.getTransactionHistory(walletId, null, null, null);

        assertEquals(4, result.size());
    }

    @Test
    void testGetTransactionHistoryWithNoTransactions() {
        Long walletId = 3L;

        when(intraTransactionRepository.findByWalletIdAndTransactionType(walletId, null)).thenReturn(new ArrayList<>());
        when(interTransactionRepository.findByWalletIdAndTransactionType(walletId, null)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactionHistory(walletId, null, null, null);

        assertEquals(0, result.size());
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetTransactionHistoryWithSortAscending() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.WITHDRAWAL, 10.0));

        List<InterTransaction> interTransactions = new ArrayList<>();
        interTransactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 100.0));
        interTransactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 150.0));

        when(intraTransactionRepository.findByWalletIdAndTransactionType(walletId, null)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletIdAndTransactionType(walletId, null)).thenReturn(interTransactions);

        List<Object> result = transactionService.getTransactionHistory(walletId, "timestamp",  "ASC", null);

        assertEquals(4, result.size());
    }

    @Test
    void testGetTransactionHistoryWithSortDescending() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.WITHDRAWAL, 10.0));

        List<InterTransaction> interTransactions = new ArrayList<>();
        interTransactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 100.0));
        interTransactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 150.0));

        when(intraTransactionRepository.findByWalletIdAndTransactionType(walletId, null)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletIdAndTransactionType(walletId, null)).thenReturn(interTransactions);

        List<Object> result = transactionService.getTransactionHistory(walletId, "timestamp",  "DESC", null);

        assertEquals(4, result.size());
    }

    @Test
    void testGetTransactionHistoryWithTypeDeposit() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));

        when(intraTransactionRepository.findByWalletIdAndTransactionType(walletId, TransactionType.DEPOSIT)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletIdAndTransactionType(walletId, TransactionType.DEPOSIT)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactionHistory(walletId, null, null, TransactionType.DEPOSIT);

        assertEquals(1, result.size());
        verify(intraTransactionRepository, times(1)).findByWalletIdAndTransactionType(walletId, TransactionType.DEPOSIT);
        verify(interTransactionRepository, times(1)).findByWalletIdAndTransactionType(walletId, TransactionType.DEPOSIT);
    }

    @Test
    void testGetTransactionHistoryWithTypeWithdrawal() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.WITHDRAWAL, 100.0));

        when(intraTransactionRepository.findByWalletIdAndTransactionType(walletId, TransactionType.WITHDRAWAL)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletIdAndTransactionType(walletId, TransactionType.WITHDRAWAL)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactionHistory(walletId, null, null, TransactionType.WITHDRAWAL);

        assertEquals(1, result.size());
        verify(intraTransactionRepository, times(1)).findByWalletIdAndTransactionType(walletId, TransactionType.WITHDRAWAL);
        verify(interTransactionRepository, times(1)).findByWalletIdAndTransactionType(walletId, TransactionType.WITHDRAWAL);
    }

    @Test
    void testGetTransactionHistoryWithTypeTransfer() {
        Long walletId = 2L;

        List<InterTransaction> interTransactions = new ArrayList<>();
        interTransactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 100.0));

        when(intraTransactionRepository.findByWalletIdAndTransactionType(walletId, TransactionType.TRANSFER)).thenReturn(new ArrayList<>());
        when(interTransactionRepository.findByWalletIdAndTransactionType(walletId, TransactionType.TRANSFER)).thenReturn(interTransactions);

        List<Object> result = transactionService.getTransactionHistory(walletId, null, null, TransactionType.TRANSFER);

        assertEquals(1, result.size());
        verify(intraTransactionRepository, times(1)).findByWalletIdAndTransactionType(walletId, TransactionType.TRANSFER);
        verify(interTransactionRepository, times(1)).findByWalletIdAndTransactionType(walletId, TransactionType.TRANSFER);
    }

    @Test
    void testGetTransactionHistoryWithSortAscendingAndTypeTransfer() {
        Long walletId = 2L;

        List<InterTransaction> interTransactions = new ArrayList<>();
        interTransactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 100.0));
        interTransactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 200.0));

        when(intraTransactionRepository.findByWalletIdAndTransactionType(walletId, TransactionType.TRANSFER)).thenReturn(new ArrayList<>());
        when(interTransactionRepository.findByWalletIdAndTransactionType(walletId, TransactionType.TRANSFER)).thenReturn(interTransactions);

        List<Object> result = transactionService.getTransactionHistory(walletId, "timestamp",  "ASC", TransactionType.TRANSFER);

        assertEquals(2, result.size());
        verify(intraTransactionRepository, times(1)).findByWalletIdAndTransactionType(walletId, TransactionType.TRANSFER);
        verify(interTransactionRepository, times(1)).findByWalletIdAndTransactionType(walletId, TransactionType.TRANSFER);
    }

    @Test
    void testGetTransactionHistoryWithSortDescendingAndTypeDeposit() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 200.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));

        when(intraTransactionRepository.findByWalletIdAndTransactionType(walletId, TransactionType.DEPOSIT)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletIdAndTransactionType(walletId, TransactionType.DEPOSIT)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactionHistory(walletId, "timestamp",  "DESC", TransactionType.DEPOSIT);

        assertEquals(2, result.size());
        verify(intraTransactionRepository, times(1)).findByWalletIdAndTransactionType(walletId, TransactionType.DEPOSIT);
        verify(interTransactionRepository, times(1)).findByWalletIdAndTransactionType(walletId, TransactionType.DEPOSIT);
    }

    @Test
    void testGetTransactionHistoryWhenSortByAmountAndDescending() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 200.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));

        when(intraTransactionRepository.findByWalletIdAndTransactionType(walletId, null)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletIdAndTransactionType(walletId, null)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactionHistory(walletId, "amount", "DESC", null);
        assertEquals(2, result.size());

        verify(intraTransactionRepository, times(1)).findByWalletIdAndTransactionType(walletId, null);
        verify(interTransactionRepository, times(1)).findByWalletIdAndTransactionType(walletId, null);
    }

    @Test
    void testGetTransactionHistoryWhenSortByAmountAndTimestampDescending() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 200.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));

        when(intraTransactionRepository.findByWalletIdAndTransactionType(walletId, null)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletIdAndTransactionType(walletId, null)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactionHistory(walletId, "amount,timestamp", "DESC,DESC", null);
        assertEquals(2, result.size());

        verify(intraTransactionRepository, times(1)).findByWalletIdAndTransactionType(walletId, null);
        verify(interTransactionRepository, times(1)).findByWalletIdAndTransactionType(walletId, null);
    }

    @Test
    void testGetTransactionHistoryWhenSortByInvalidField() {
        Long walletId = 2L;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.getTransactionHistory(walletId, "amounts", "DESC", null);
        });
        assertEquals("Invalid sort field: amounts", exception.getMessage());
        verifyNoMoreInteractions(intraTransactionRepository, interTransactionRepository);
    }

    @Test
    void testGetTransactionHistoryWhenSortOrderInvalid() {
        Long walletId = 2L;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.getTransactionHistory(walletId, "amount", "DES", null);
        });

        assertEquals("Invalid sort order: DES", exception.getMessage());
        verifyNoMoreInteractions(intraTransactionRepository, interTransactionRepository);
    }

    @Test
    void testGetTransactionHistoryWhenSortOrderLengthIsLessThanSortByLength() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 200.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));

        when(intraTransactionRepository.findByWalletIdAndTransactionType(walletId, null)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletIdAndTransactionType(walletId, null)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactionHistory(walletId, "amount,timestamp", "DESC", null);

        assertEquals(2, result.size());
        verify(intraTransactionRepository, times(1)).findByWalletIdAndTransactionType(walletId, null);
        verify(interTransactionRepository, times(1)).findByWalletIdAndTransactionType(walletId, null);
    }

    @Test
    void testGetTransactionHistoryWhenSortOrderLengthIsGreaterThanSortByLength() {
        Long walletId = 2L;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.getTransactionHistory(walletId, "amount", "DESC,ASC", null);
        });

        assertEquals("The number of sort fields must be greater than or equal to the number of sort orders", exception.getMessage());
        verifyNoMoreInteractions(intraTransactionRepository, interTransactionRepository);
    }

    @Test
    void testGetTransactionHistoryWhenSortByUpperCaseAmount() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 200.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));

        when(intraTransactionRepository.findByWalletIdAndTransactionType(walletId, null)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletIdAndTransactionType(walletId, null)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactionHistory(walletId, "AMOUNT", "DESC", null);
        assertEquals(2, result.size());

        verify(intraTransactionRepository, times(1)).findByWalletIdAndTransactionType(walletId, null);
        verify(interTransactionRepository, times(1)).findByWalletIdAndTransactionType(walletId, null);
    }

    @Test
    void testGetTransactionHistoryWhenSortOrderLowerCaseDesc() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 200.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));

        when(intraTransactionRepository.findByWalletIdAndTransactionType(walletId, null)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletIdAndTransactionType(walletId, null)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactionHistory(walletId, "amount", "desc", null);
        assertEquals(2, result.size());

        verify(intraTransactionRepository, times(1)).findByWalletIdAndTransactionType(walletId, null);
        verify(interTransactionRepository, times(1)).findByWalletIdAndTransactionType(walletId, null);
    }

}
