package com.example.wallet.service;

import com.example.wallet.Enums.TransactionType;
import com.example.wallet.Exceptions.UserNotAuthorizedException;
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
    private Long userId;

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private WalletService walletService;

    @Mock
    private IntraTransactionRepository intraTransactionRepository;

    @Mock
    private InterTransactionRepository interTransactionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = 40L;
    }

    @Test
    void testDepositWhenWalletDoesNotBelongToUser() {
        Long walletId = 1L;

        when(walletService.isUserAuthorized(userId, walletId)).thenReturn(false);

        assertThrows(UserNotAuthorizedException.class, () ->
                transactionService.getTransactionHistory(userId, walletId, null, null, null)
        );
    }

    @Test
    void testGetTransactionHistoryWithIntraTransactions() {
        Long walletId = 1L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));

        when(walletService.isUserAuthorized(userId, walletId)).thenReturn(true);
        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletId(walletId)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactionHistory(userId, walletId, null, null, null);

        assertEquals(1, result.size());
    }

    @Test
    void testGetTransactionHistoryWithInterTransactions() {
        Long walletId = 2L;

        List<InterTransaction> interTransactions = new ArrayList<>();
        interTransactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 100.0));

        when(walletService.isUserAuthorized(userId, walletId)).thenReturn(true);
        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(new ArrayList<>());
        when(interTransactionRepository.findByWalletId(walletId)).thenReturn(interTransactions);

        List<Object> result = transactionService.getTransactionHistory(userId, walletId, null, null, null);

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

        when(walletService.isUserAuthorized(userId, walletId)).thenReturn(true);
        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletId(walletId)).thenReturn(interTransactions);

        List<Object> result = transactionService.getTransactionHistory(userId, walletId, null, null, null);

        assertEquals(4, result.size());
    }

    @Test
    void testGetTransactionHistoryWithNoTransactions() {
        Long walletId = 3L;

        when(walletService.isUserAuthorized(userId, walletId)).thenReturn(true);
        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(new ArrayList<>());
        when(interTransactionRepository.findByWalletId(walletId)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactionHistory(userId, walletId, null, null, null);

        assertEquals(0, result.size());
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetTransactionHistoryWithSortTimestampAscending() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.WITHDRAWAL, 10.0));

        List<InterTransaction> interTransactions = new ArrayList<>();
        interTransactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 100.0));
        interTransactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 150.0));

        when(walletService.isUserAuthorized(userId, walletId)).thenReturn(true);
        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletId(walletId)).thenReturn(interTransactions);

        List<Object> result = transactionService.getTransactionHistory(userId, walletId, "timestamp",  "ASC", null);

        assertEquals(4, result.size());
    }

    @Test
    void testGetTransactionHistoryWithSortTimestampDescending() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.WITHDRAWAL, 10.0));

        List<InterTransaction> interTransactions = new ArrayList<>();
        interTransactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 100.0));
        interTransactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 150.0));

        when(walletService.isUserAuthorized(userId, walletId)).thenReturn(true);
        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletId(walletId)).thenReturn(interTransactions);

        List<Object> result = transactionService.getTransactionHistory(userId, walletId, "timestamp",  "DESC", null);

        assertEquals(4, result.size());
    }

    @Test
    void testGetTransactionHistoryWithTypeDeposit() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));

        when(walletService.isUserAuthorized(userId, walletId)).thenReturn(true);
        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletId(walletId)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactionHistory(userId, walletId, null, null, "DEPOSIT");

        assertEquals(1, result.size());
        verify(intraTransactionRepository, times(1)).findByWalletId(walletId);
        verify(interTransactionRepository, times(1)).findByWalletId(walletId);
    }

    @Test
    void testGetTransactionHistoryWithTypeWithdrawal() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.WITHDRAWAL, 100.0));

        when(walletService.isUserAuthorized(userId, walletId)).thenReturn(true);
        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletId(walletId)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactionHistory(userId, walletId, null, null, "WITHDRAWAL");

        assertEquals(1, result.size());
        verify(intraTransactionRepository, times(1)).findByWalletId(walletId);
        verify(interTransactionRepository, times(1)).findByWalletId(walletId);
    }

    @Test
    void testGetTransactionHistoryWithTypeTransfer() {
        Long walletId = 2L;

        List<InterTransaction> interTransactions = new ArrayList<>();
        interTransactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 100.0));

        when(walletService.isUserAuthorized(userId, walletId)).thenReturn(true);
        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(new ArrayList<>());
        when(interTransactionRepository.findByWalletId(walletId)).thenReturn(interTransactions);

        List<Object> result = transactionService.getTransactionHistory(userId, walletId, null, null, "TRANSFER");

        assertEquals(1, result.size());
        verify(intraTransactionRepository, times(1)).findByWalletId(walletId);
        verify(interTransactionRepository, times(1)).findByWalletId(walletId);
    }

    @Test
    void testGetTransactionHistoryWithSortAscendingAndTypeTransfer() {
        Long walletId = 2L;

        List<InterTransaction> interTransactions = new ArrayList<>();
        interTransactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 100.0));
        interTransactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 200.0));

        when(walletService.isUserAuthorized(userId, walletId)).thenReturn(true);
        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(new ArrayList<>());
        when(interTransactionRepository.findByWalletId(walletId)).thenReturn(interTransactions);

        List<Object> result = transactionService.getTransactionHistory(userId, walletId, "timestamp",  "ASC", "TRANSFER");

        assertEquals(2, result.size());
        verify(intraTransactionRepository, times(1)).findByWalletId(walletId);
        verify(interTransactionRepository, times(1)).findByWalletId(walletId);
    }

    @Test
    void testGetTransactionHistoryWithSortDescendingAndTypeDeposit() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 200.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));

        when(walletService.isUserAuthorized(userId, walletId)).thenReturn(true);
        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletId(walletId)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactionHistory(userId, walletId, "timestamp",  "DESC", "DEPOSIT");

        assertEquals(2, result.size());
        verify(intraTransactionRepository, times(1)).findByWalletId(walletId);
        verify(interTransactionRepository, times(1)).findByWalletId(walletId);
    }

    @Test
    void testGetTransactionHistoryWhenSortByAmountAndDescending() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 200.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));

        when(walletService.isUserAuthorized(userId, walletId)).thenReturn(true);
        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletId(walletId)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactionHistory(userId, walletId, "amount", "DESC", null);
        assertEquals(2, result.size());

        verify(intraTransactionRepository, times(1)).findByWalletId(walletId);
        verify(interTransactionRepository, times(1)).findByWalletId(walletId);
    }

    @Test
    void testGetTransactionHistoryWhenSortByAmountAndTimestampDescending() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 200.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));

        when(walletService.isUserAuthorized(userId, walletId)).thenReturn(true);
        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletId(walletId)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactionHistory(userId, walletId, "amount,timestamp", "DESC,DESC", null);
        assertEquals(2, result.size());

        verify(intraTransactionRepository, times(1)).findByWalletId(walletId);
        verify(interTransactionRepository, times(1)).findByWalletId(walletId);
    }

    @Test
    void testGetTransactionHistoryWhenSortByInvalidField() {
        Long walletId = 2L;

        when(walletService.isUserAuthorized(userId, walletId)).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.getTransactionHistory(userId, walletId, "amounts", "DESC", null);
        });
        assertEquals("Invalid sort field: amounts", exception.getMessage());
        verifyNoInteractions(intraTransactionRepository, interTransactionRepository);
    }

    @Test
    void testGetTransactionHistoryWhenSortOrderInvalid() {
        Long walletId = 2L;

        when(walletService.isUserAuthorized(userId, walletId)).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.getTransactionHistory(userId, walletId, "amount", "DES", null);
        });

        assertEquals("Invalid sort order: DES", exception.getMessage());
        verifyNoInteractions(intraTransactionRepository, interTransactionRepository);
    }

    @Test
    void testGetTransactionHistoryWhenSortOrderLengthIsLessThanSortByLength() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 200.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));

        when(walletService.isUserAuthorized(userId, walletId)).thenReturn(true);
        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletId(walletId)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactionHistory(userId, walletId, "amount,timestamp", "DESC", null);

        assertEquals(2, result.size());
        verify(intraTransactionRepository, times(1)).findByWalletId(walletId);
        verify(interTransactionRepository, times(1)).findByWalletId(walletId);
    }

    @Test
    void testGetTransactionHistoryWhenSortOrderLengthIsGreaterThanSortByLength() {
        Long walletId = 2L;

        when(walletService.isUserAuthorized(userId, walletId)).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.getTransactionHistory(userId, walletId, "amount", "DESC,ASC", null);
        });

        assertEquals("The number of sort fields must be greater than or equal to the number of sort orders", exception.getMessage());
        verifyNoInteractions(intraTransactionRepository, interTransactionRepository);
    }

    @Test
    void testGetTransactionHistoryWhenSortByUpperCaseAmount() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 200.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));

        when(walletService.isUserAuthorized(userId, walletId)).thenReturn(true);
        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletId(walletId)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactionHistory(userId, walletId, "AMOUNT", "DESC", null);
        assertEquals(2, result.size());

        verify(intraTransactionRepository, times(1)).findByWalletId(walletId);
        verify(interTransactionRepository, times(1)).findByWalletId(walletId);
    }

    @Test
    void testGetTransactionHistoryWhenSortOrderLowerCaseDesc() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 200.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));

        when(walletService.isUserAuthorized(userId, walletId)).thenReturn(true);
        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletId(walletId)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactionHistory(userId, walletId, "amount", "desc", null);
        assertEquals(2, result.size());

        verify(intraTransactionRepository, times(1)).findByWalletId(walletId);
        verify(interTransactionRepository, times(1)).findByWalletId(walletId);
    }

    @Test
    void testGetTransactionHistoryWithTypeDepositAndTransfer() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 200.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.WITHDRAWAL, 50.0));

        when(walletService.isUserAuthorized(userId, walletId)).thenReturn(true);
        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletId(walletId)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactionHistory(userId, walletId, null, null, "DEPOSIT,TRANSFER");

        assertEquals(2, result.size());
        verify(intraTransactionRepository, times(1)).findByWalletId(walletId);
        verify(interTransactionRepository, times(1)).findByWalletId(walletId);
    }

    @Test
    void testGetTransactionHistoryWithTransferTypeDepositTransferAndWithdrawal() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 200.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.WITHDRAWAL, 50.0));

        when(walletService.isUserAuthorized(userId, walletId)).thenReturn(true);
        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletId(walletId)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactionHistory(userId, walletId, null, null, "DEPOSIT,TRANSFER,WITHDRAWAL");

        assertEquals(3, result.size());
        verify(intraTransactionRepository, times(1)).findByWalletId(walletId);
        verify(interTransactionRepository, times(1)).findByWalletId(walletId);
    }

    @Test
    void testGetTransactionHistoryWithTransferTypeDepositAndTransferLowerCase() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 200.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.WITHDRAWAL, 50.0));

        when(walletService.isUserAuthorized(userId, walletId)).thenReturn(true);
        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletId(walletId)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactionHistory(userId, walletId, null, null, "DEPOSIT,Transfer");

        assertEquals(2, result.size());
        verify(intraTransactionRepository, times(1)).findByWalletId(walletId);
        verify(interTransactionRepository, times(1)).findByWalletId(walletId);
    }

    @Test
    void testGetTransactionHistoryWithTransferTypeInvalid() {
        Long walletId = 2L;

        when(walletService.isUserAuthorized(userId, walletId)).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.getTransactionHistory(userId, walletId, null, null, "DEPOSIT,Transf");
        });

        assertEquals("Invalid transaction type: Transf", exception.getMessage());
        verifyNoInteractions(intraTransactionRepository, interTransactionRepository);
    }
}
