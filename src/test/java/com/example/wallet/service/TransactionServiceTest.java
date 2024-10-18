package com.example.wallet.service;

import com.example.wallet.Enums.CurrencyType;
import com.example.wallet.Enums.TransactionType;
import com.example.wallet.Exceptions.*;
import com.example.wallet.model.InterTransaction;
import com.example.wallet.model.IntraTransaction;
import com.example.wallet.model.User;
import com.example.wallet.model.Wallet;
import com.example.wallet.repository.InterTransactionRepository;
import com.example.wallet.repository.IntraTransactionRepository;
import com.example.wallet.repository.UserRepository;
import com.example.wallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {
    private Long userId;
    private Long walletId;
    private Wallet wallet, mockWallet;

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private WalletRepository walletRepository;
    @Mock
    private IntraTransactionRepository intraTransactionRepository;
    @Mock
    private InterTransactionRepository interTransactionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        walletId = 1L;
        wallet = new Wallet();
        userId = 10L;
        mockWallet = mock(Wallet.class);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testCreateTransactionWhenTransactionTypeIsDeposit() {
        Double depositAmount = 100.0;
        User user = new User("testUser", "password", CurrencyType.INR);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(mockWallet));

        transactionService.createTransaction(userId, walletId, null, depositAmount, "deposit");

        verify(mockWallet, times(1)).deposit(depositAmount);
        verify(intraTransactionRepository, times(1)).save(any(IntraTransaction.class));
    }

    @Test
    void testCreateTransactionWhenTransactionTypeIsWithdrawal() {
        Double withdrawAmount = 100.0;
        User user = new User("testUser", "password", CurrencyType.INR);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(mockWallet));

        transactionService.createTransaction(userId, walletId, null, withdrawAmount, "withdrawal");

        verify(mockWallet, times(1)).withdraw(withdrawAmount);
        verify(intraTransactionRepository, times(1)).save(any(IntraTransaction.class));
    }

    @Test
    void testCreateTransactionWhenTransactionTypeIsTransfer() {
        Long recipientWalletId = 5L;
        Double transferAmount = 100.0;
        User user = new User("testUser", "password", CurrencyType.INR);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(mockWallet));
        when(walletRepository.findById(recipientWalletId)).thenReturn(Optional.of(wallet));

        transactionService.createTransaction(userId, walletId, 5L, transferAmount, "transfer");

        verify(mockWallet, times(1)).transfer(wallet, transferAmount);
        verify(interTransactionRepository, times(1)).save(any(InterTransaction.class));
    }

    @Test
    void testCreateTransactionWhenTransactionTypeIsInvalid() {
        Long recipientWalletId = 5L;
        Double transferAmount = 100.0;

        assertThrows(InvalidTransactionTypeException.class, () -> transactionService.createTransaction(userId, walletId, recipientWalletId, transferAmount, "invalid"));

        verify(interTransactionRepository, times(0)).save(any(InterTransaction.class));
        verify(intraTransactionRepository, times(0)).save(any(IntraTransaction.class));
    }

    @Test
    void testDeposit() {
        Double depositAmount = 100.0;
        User user = new User("testUser", "password", CurrencyType.INR);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        Double newBalance = transactionService.deposit(userId, walletId, depositAmount);

        assertEquals(depositAmount, newBalance);
        verify(walletRepository, times(1)).findById(walletId);
        verify(intraTransactionRepository, times(1)).save(any(IntraTransaction.class));
    }

    @Test
    void testDepositNegativeAmount() {
        Double depositAmount = -100.0;
        User user = new User("testUser", "password", CurrencyType.INR);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        assertThrows(DepositAmountMustBePositiveException.class, () ->
                transactionService.deposit(userId, walletId, depositAmount)
        );
        verify(walletRepository, times(1)).findById(walletId);
        verify(intraTransactionRepository, times(0)).save(any(IntraTransaction.class));
    }

    @Test
    void testDepositWhenUserNotFound() {
        Double depositAmount = 100.0;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                transactionService.deposit(userId, walletId, depositAmount)
        );
        verify(userRepository, times(1)).findById(userId);
        verify(walletRepository, times(0)).findById(walletId);
        verify(intraTransactionRepository, times(0)).save(any(IntraTransaction.class));
    }

    @Test
    void testDepositWhenWalletDoesNotBelongToUser() {
        Long invalidWalletId = 2L;
        Double depositAmount = 100.0;
        User user = new User("testUser", "password", CurrencyType.INR);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));

        assertThrows(UserNotAuthorizedException.class, () ->
                transactionService.deposit(userId, invalidWalletId, depositAmount)
        );
        verify(intraTransactionRepository, times(0)).save(any(IntraTransaction.class));
    }

    @Test
    void testWithdrawWithSufficientFunds() {
        Double withdrawAmount = 50.0;
        User user = new User("testUser", "password", CurrencyType.INR);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        transactionService.deposit(userId, walletId, 100.0);

        Double newBalance = transactionService.withdraw(userId, walletId, withdrawAmount);

        assertEquals(50.0, newBalance);
        verify(walletRepository, times(2)).findById(walletId);
        verify(intraTransactionRepository, times(2)).save(any(IntraTransaction.class));
    }

    @Test
    void testWithdrawNegativeAmount() {
        Double withdrawAmount = -150.0;
        User user = new User("testUser", "password", CurrencyType.INR);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        transactionService.deposit(userId, walletId, 100.0);

        assertThrows(WithdrawAmountMustBePositiveException.class, () ->
                transactionService.withdraw(userId, walletId, withdrawAmount)
        );
        verify(walletRepository, times(2)).findById(walletId);
        verify(intraTransactionRepository, times(1)).save(any(IntraTransaction.class));
    }

    @Test
    void testWithdrawWithInsufficientFunds() {
        Double withdrawAmount = 150.0;
        User user = new User("testUser", "password", CurrencyType.INR);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        transactionService.deposit(userId, walletId, 100.0);

        assertThrows(InsufficientFundsException.class, () ->
                transactionService.withdraw(userId, walletId, withdrawAmount)
        );
        verify(walletRepository, times(2)).findById(walletId);
        verify(intraTransactionRepository, times(1)).save(any(IntraTransaction.class));
    }

    @Test
    void testWithdrawWhenUserNotFound() {
        Double withdrawAmount = 100.0;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                transactionService.withdraw(userId, walletId, withdrawAmount)
        );
        verify(userRepository, times(1)).findById(userId);
        verify(walletRepository, times(0)).findById(walletId);
        verify(intraTransactionRepository, times(0)).save(any(IntraTransaction.class));
    }

    @Test
    void testWithdrawWhenWalletDoesNotBelongToUser() {
        Long invalidWalletId = 2L;
        Double withdrawAmount = 100.0;
        User user = new User("testUser", "password", CurrencyType.INR);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));

        assertThrows(UserNotAuthorizedException.class, () ->
                transactionService.withdraw(userId, invalidWalletId, withdrawAmount)
        );
        verify(intraTransactionRepository, times(0)).save(any(IntraTransaction.class));
    }

    @Test
    void testTransferSuccessful() {
        Long senderWalletId = 1L;
        Long recipientWalletId = 2L;
        Double transferAmount = 30.0;
        User user = new User("testUser", "password", CurrencyType.INR);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));
        when(walletRepository.findById(senderWalletId)).thenReturn(Optional.of(new Wallet()));
        when(walletRepository.findById(recipientWalletId)).thenReturn(Optional.of(new Wallet()));

        transactionService.deposit(userId, senderWalletId, 100.0);
        Double newBalance = transactionService.transfer(userId, senderWalletId, recipientWalletId, transferAmount);

        assertEquals(70.0, newBalance);
        verify(walletRepository, times(2)).findById(senderWalletId);
        verify(walletRepository, times(1)).findById(recipientWalletId);
        verify(intraTransactionRepository, times(1)).save(any(IntraTransaction.class));
        verify(interTransactionRepository, times(1)).save(any(InterTransaction.class));
    }

    @Test
    void testTransferUserNotFoundForSender() {
        Long invalidSenderWalletId = 1L;
        Long recipientWalletId = 2L;
        Double transferAmount = 30.0;

        User user = new User("testUser", "password", CurrencyType.INR);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));
        when(walletRepository.findById(invalidSenderWalletId)).thenReturn(Optional.empty());
        when(walletRepository.findById(recipientWalletId)).thenReturn(Optional.of(new Wallet()));

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            transactionService.transfer(userId, invalidSenderWalletId, recipientWalletId, transferAmount);
        });

        assertEquals("Sender not found", exception.getMessage());
        verify(walletRepository, times(1)).findById(invalidSenderWalletId);
        verify(walletRepository, times(0)).findById(recipientWalletId);
        verify(interTransactionRepository, times(0)).save(any(InterTransaction.class));
    }

    @Test
    void testTransferWalletDoesNotBelongToSender() {
        Long senderWalletId = 1L;
        Long invalidSenderWalletId = 15L;
        Long recipientWalletId = 2L;
        Double transferAmount = 30.0;

        User user = new User("testUser", "password", CurrencyType.INR);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(senderWalletId));

        assertThrows(UserNotAuthorizedException.class, () ->
                transactionService.transfer(userId, invalidSenderWalletId, recipientWalletId, transferAmount)
        );
        verify(intraTransactionRepository, times(0)).save(any(IntraTransaction.class));
    }

    @Test
    void testTransferUserNotFoundForRecipient() {
        Long senderWalletId = 1L;
        Long invalidRecipientWalletId = 2L;
        Double transferAmount = 30.0;

        User user = new User("testUser", "password", CurrencyType.INR);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));
        when(walletRepository.findById(senderWalletId)).thenReturn(Optional.of(new Wallet()));
        when(walletRepository.findById(invalidRecipientWalletId)).thenReturn(Optional.empty());

        transactionService.deposit(userId, senderWalletId, 100.0);
        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            transactionService.transfer(userId, senderWalletId, invalidRecipientWalletId, transferAmount);
        });

        assertEquals("Recipient not found", exception.getMessage());
        verify(walletRepository, times(2)).findById(senderWalletId);
        verify(walletRepository, times(1)).findById(invalidRecipientWalletId);
        verify(intraTransactionRepository, times(1)).save(any(IntraTransaction.class));
        verify(interTransactionRepository, times(0)).save(any(InterTransaction.class));
    }

    @Test
    void testTransferInsufficientFunds() {
        Long senderWalletId = 1L;
        Long recipientWalletId = 2L;
        Double transferAmount = 130.0;

        User user = new User("testUser", "password", CurrencyType.INR);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));
        when(walletRepository.findById(senderWalletId)).thenReturn(Optional.of(new Wallet()));
        when(walletRepository.findById(recipientWalletId)).thenReturn(Optional.of(new Wallet()));

        transactionService.deposit(userId, senderWalletId, 100.0);
        Exception exception = assertThrows(InsufficientFundsException.class, () -> {
            transactionService.transfer(userId, senderWalletId, recipientWalletId, transferAmount);
        });

        assertEquals("Insufficient funds for transfer", exception.getMessage());
        verify(walletRepository, times(2)).findById(senderWalletId);
        verify(walletRepository, times(1)).findById(recipientWalletId);
        verify(intraTransactionRepository, times(1)).save(any(IntraTransaction.class));
        verify(interTransactionRepository, times(0)).save(any(InterTransaction.class));
    }

    @Test
    void testTransferAmountMustBePositive() {
        Long senderWalletId = 1L;
        Long recipientWalletId = 2L;
        Double transferAmount = -10.0;

        User user = new User("testUser", "password", CurrencyType.INR);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));
        when(walletRepository.findById(senderWalletId)).thenReturn(Optional.of(new Wallet()));
        when(walletRepository.findById(recipientWalletId)).thenReturn(Optional.of(new Wallet()));

        transactionService.deposit(userId, senderWalletId, 100.0);
        Exception exception = assertThrows(TransferAmountMustBePositiveException.class, () -> {
            transactionService.transfer(userId, senderWalletId, recipientWalletId, transferAmount);
        });

        assertEquals("Transfer amount must be positive", exception.getMessage());
        verify(walletRepository, times(2)).findById(senderWalletId);
        verify(walletRepository, times(1)).findById(recipientWalletId);
        verify(intraTransactionRepository, times(1)).save(any(IntraTransaction.class));
        verify(interTransactionRepository, times(0)).save(any(InterTransaction.class));
    }

    @Test
    void testGetTransactionsWhenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                transactionService.getTransactions(userId, walletId, null, null, null)
        );
    }

    @Test
    void testGetTransactionsWhenUserIsNotAuthorized() {
        Long invalidWalletId = 101L;
        User user = new User("testUser", "password", CurrencyType.INR);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));

        assertThrows(UserNotAuthorizedException.class, () ->
                transactionService.getTransactions(userId, invalidWalletId, null, null, null)
        );
    }

    @Test
    void testGetTransactionsWithIntraTransactions() {
        Long walletId = 1L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));

        User user = new User("testUser", "password", CurrencyType.INR);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));
        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletId(walletId)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactions(userId, walletId, null, null, null);

        assertEquals(1, result.size());
    }

    @Test
    void testGetTransactionsWithInterTransactions() {
        Long walletId = 2L;

        List<InterTransaction> interTransactions = new ArrayList<>();
        interTransactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 100.0));

        User user = new User("testUser", "password", CurrencyType.INR);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));
        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(new ArrayList<>());
        when(interTransactionRepository.findByWalletId(walletId)).thenReturn(interTransactions);

        List<Object> result = transactionService.getTransactions(userId, walletId, null, null, null);

        assertEquals(1, result.size());
    }

    @Test
    void testGetTransactionsWithBothIntraAndInterTransactions() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.WITHDRAWAL, 10.0));

        List<InterTransaction> interTransactions = new ArrayList<>();
        interTransactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 100.0));
        interTransactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 150.0));

        User user = new User("testUser", "password", CurrencyType.INR);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));
        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletId(walletId)).thenReturn(interTransactions);

        List<Object> result = transactionService.getTransactions(userId, walletId, null, null, null);

        assertEquals(4, result.size());
    }

    @Test
    void testGetTransactionsWithNoTransactions() {
        Long walletId = 3L;

        User user = new User("testUser", "password", CurrencyType.INR);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));
        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(new ArrayList<>());
        when(interTransactionRepository.findByWalletId(walletId)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactions(userId, walletId, null, null, null);

        assertEquals(0, result.size());
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetTransactionsWithSortTimestampAscending() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.WITHDRAWAL, 10.0));

        List<InterTransaction> interTransactions = new ArrayList<>();
        interTransactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 100.0));
        interTransactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 150.0));

        User user = new User("testUser", "password", CurrencyType.INR);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));
        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletId(walletId)).thenReturn(interTransactions);

        List<Object> result = transactionService.getTransactions(userId, walletId, "timestamp",  "ASC", null);

        assertEquals(4, result.size());
    }

    @Test
    void testGetTransactionsWithSortTimestampDescending() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.WITHDRAWAL, 10.0));

        List<InterTransaction> interTransactions = new ArrayList<>();
        interTransactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 100.0));
        interTransactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 150.0));

        User user = new User("testUser", "password", CurrencyType.INR);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));
        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletId(walletId)).thenReturn(interTransactions);

        List<Object> result = transactionService.getTransactions(userId, walletId, "timestamp",  "DESC", null);

        assertEquals(4, result.size());
    }

    @Test
    void testGetTransactionsWithTypeDeposit() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));

        User user = new User("testUser", "password", CurrencyType.INR);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));
        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletId(walletId)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactions(userId, walletId, null, null, "DEPOSIT");

        assertEquals(1, result.size());
        verify(intraTransactionRepository, times(1)).findByWalletId(walletId);
        verify(interTransactionRepository, times(1)).findByWalletId(walletId);
    }

    @Test
    void testGetTransactionsWithTypeWithdrawal() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.WITHDRAWAL, 100.0));

        User user = new User("testUser", "password", CurrencyType.INR);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));
        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletId(walletId)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactions(userId, walletId, null, null, "WITHDRAWAL");

        assertEquals(1, result.size());
        verify(intraTransactionRepository, times(1)).findByWalletId(walletId);
        verify(interTransactionRepository, times(1)).findByWalletId(walletId);
    }

    @Test
    void testGetTransactionsWithTypeTransfer() {
        Long walletId = 2L;

        List<InterTransaction> interTransactions = new ArrayList<>();
        interTransactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 100.0));

        User user = new User("testUser", "password", CurrencyType.INR);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));
        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(new ArrayList<>());
        when(interTransactionRepository.findByWalletId(walletId)).thenReturn(interTransactions);

        List<Object> result = transactionService.getTransactions(userId, walletId, null, null, "TRANSFER");

        assertEquals(1, result.size());
        verify(intraTransactionRepository, times(1)).findByWalletId(walletId);
        verify(interTransactionRepository, times(1)).findByWalletId(walletId);
    }

    @Test
    void testGetTransactionsWithSortAscendingAndTypeTransfer() {
        Long walletId = 2L;

        List<InterTransaction> interTransactions = new ArrayList<>();
        interTransactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 100.0));
        interTransactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 200.0));

        User user = new User("testUser", "password", CurrencyType.INR);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));
        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(new ArrayList<>());
        when(interTransactionRepository.findByWalletId(walletId)).thenReturn(interTransactions);

        List<Object> result = transactionService.getTransactions(userId, walletId, "timestamp",  "ASC", "TRANSFER");

        assertEquals(2, result.size());
        verify(intraTransactionRepository, times(1)).findByWalletId(walletId);
        verify(interTransactionRepository, times(1)).findByWalletId(walletId);
    }

    @Test
    void testGetTransactionsWithSortDescendingAndTypeDeposit() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 200.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));

        User user = new User("testUser", "password", CurrencyType.INR);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));
        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletId(walletId)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactions(userId, walletId, "timestamp",  "DESC", "DEPOSIT");

        assertEquals(2, result.size());
        verify(intraTransactionRepository, times(1)).findByWalletId(walletId);
        verify(interTransactionRepository, times(1)).findByWalletId(walletId);
    }

    @Test
    void testGetTransactionsWhenSortByAmountAndDescending() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 200.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));

        User user = new User("testUser", "password", CurrencyType.INR);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));
        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletId(walletId)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactions(userId, walletId, "amount", "DESC", null);
        assertEquals(2, result.size());

        verify(intraTransactionRepository, times(1)).findByWalletId(walletId);
        verify(interTransactionRepository, times(1)).findByWalletId(walletId);
    }

    @Test
    void testGetTransactionsWhenSortByAmountAndTimestampDescending() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 200.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));

        User user = new User("testUser", "password", CurrencyType.INR);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));
        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletId(walletId)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactions(userId, walletId, "amount,timestamp", "DESC,DESC", null);
        assertEquals(2, result.size());

        verify(intraTransactionRepository, times(1)).findByWalletId(walletId);
        verify(interTransactionRepository, times(1)).findByWalletId(walletId);
    }

    @Test
    void testGetTransactionsWhenSortByInvalidField() {
        Long walletId = 2L;

        User user = new User("testUser", "password", CurrencyType.INR);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.getTransactions(userId, walletId, "amounts", "DESC", null);
        });
        assertEquals("Invalid sort field: amounts", exception.getMessage());
        verifyNoInteractions(intraTransactionRepository, interTransactionRepository);
    }

    @Test
    void testGetTransactionsWhenSortOrderInvalid() {
        Long walletId = 2L;

        User user = new User("testUser", "password", CurrencyType.INR);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.getTransactions(userId, walletId, "amount", "DES", null);
        });

        assertEquals("Invalid sort order: DES", exception.getMessage());
        verifyNoInteractions(intraTransactionRepository, interTransactionRepository);
    }

    @Test
    void testGetTransactionsWhenSortOrderLengthIsLessThanSortByLength() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 200.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));

        User user = new User("testUser", "password", CurrencyType.INR);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));
        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletId(walletId)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactions(userId, walletId, "amount,timestamp", "DESC", null);

        assertEquals(2, result.size());
        verify(intraTransactionRepository, times(1)).findByWalletId(walletId);
        verify(interTransactionRepository, times(1)).findByWalletId(walletId);
    }

    @Test
    void testGetTransactionsWhenSortOrderLengthIsGreaterThanSortByLength() {
        Long walletId = 2L;

        User user = new User("testUser", "password", CurrencyType.INR);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.getTransactions(userId, walletId, "amount", "DESC,ASC", null);
        });

        assertEquals("The number of sort fields must be greater than or equal to the number of sort orders", exception.getMessage());
        verifyNoInteractions(intraTransactionRepository, interTransactionRepository);
    }

    @Test
    void testGetTransactionsWhenSortByUpperCaseAmount() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 200.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));

        User user = new User("testUser", "password", CurrencyType.INR);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));
        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletId(walletId)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactions(userId, walletId, "AMOUNT", "DESC", null);
        assertEquals(2, result.size());

        verify(intraTransactionRepository, times(1)).findByWalletId(walletId);
        verify(interTransactionRepository, times(1)).findByWalletId(walletId);
    }

    @Test
    void testGetTransactionsWhenSortOrderLowerCaseDesc() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 200.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));

        User user = new User("testUser", "password", CurrencyType.INR);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));
        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletId(walletId)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactions(userId, walletId, "amount", "desc", null);
        assertEquals(2, result.size());

        verify(intraTransactionRepository, times(1)).findByWalletId(walletId);
        verify(interTransactionRepository, times(1)).findByWalletId(walletId);
    }

    @Test
    void testGetTransactionsWithTypeDepositAndTransfer() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 200.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.WITHDRAWAL, 50.0));

        User user = new User("testUser", "password", CurrencyType.INR);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));
        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletId(walletId)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactions(userId, walletId, null, null, "DEPOSIT,TRANSFER");

        assertEquals(2, result.size());
        verify(intraTransactionRepository, times(1)).findByWalletId(walletId);
        verify(interTransactionRepository, times(1)).findByWalletId(walletId);
    }

    @Test
    void testGetTransactionsWithTransferTypeDepositTransferAndWithdrawal() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 200.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.WITHDRAWAL, 50.0));

        User user = new User("testUser", "password", CurrencyType.INR);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));
        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletId(walletId)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactions(userId, walletId, null, null, "DEPOSIT,TRANSFER,WITHDRAWAL");

        assertEquals(3, result.size());
        verify(intraTransactionRepository, times(1)).findByWalletId(walletId);
        verify(interTransactionRepository, times(1)).findByWalletId(walletId);
    }

    @Test
    void testGetTransactionsWithTransferTypeDepositAndTransferLowerCase() {
        Long walletId = 2L;

        List<IntraTransaction> intraTransactions = new ArrayList<>();
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 200.0));
        intraTransactions.add(new IntraTransaction(new Wallet(), TransactionType.WITHDRAWAL, 50.0));

        User user = new User("testUser", "password", CurrencyType.INR);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));
        when(intraTransactionRepository.findByWalletId(walletId)).thenReturn(intraTransactions);
        when(interTransactionRepository.findByWalletId(walletId)).thenReturn(new ArrayList<>());

        List<Object> result = transactionService.getTransactions(userId, walletId, null, null, "DEPOSIT,Transfer");

        assertEquals(2, result.size());
        verify(intraTransactionRepository, times(1)).findByWalletId(walletId);
        verify(interTransactionRepository, times(1)).findByWalletId(walletId);
    }

    @Test
    void testGetTransactionsWithTransferTypeInvalid() {
        Long walletId = 2L;

        User user = new User("testUser", "password", CurrencyType.INR);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));

        Exception exception = assertThrows(InvalidTransactionTypeException.class, () -> {
            transactionService.getTransactions(userId, walletId, null, null, "DEPOSIT,Transf");
        });

        assertEquals("Invalid transaction type: Transf", exception.getMessage());
        verifyNoInteractions(intraTransactionRepository, interTransactionRepository);
    }
}
