package com.example.wallet.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.wallet.Exceptions.*;
import com.example.wallet.model.Transaction;
import com.example.wallet.model.Wallet;
import com.example.wallet.repository.TransactionRepository;
import com.example.wallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

public class WalletServiceTest {
    Long walletId;
    Wallet wallet;

    @InjectMocks
    private WalletService walletService;

    @Mock
    private WalletRepository walletRepository;
    @Mock
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        walletId = 1L;
        wallet = new Wallet();
    }

    @Test
    void testDeposit() {
        Double depositAmount = 100.0;
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        Double newBalance = walletService.deposit(walletId, depositAmount);

        assertEquals(depositAmount, newBalance);
        verify(walletRepository, times(1)).findById(walletId);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testDepositNegativeAmount() {
        Double depositAmount = -100.0;
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        assertThrows(DepositAmountMustBePositiveException.class, () ->
                walletService.deposit(walletId, depositAmount)
        );
        verify(walletRepository, times(1)).findById(walletId);
        verify(transactionRepository, times(0)).save(any(Transaction.class));
    }

    @Test
    void testDepositWithInvalidWalletId() {
        Long invalidWalletId = 2L;
        Double depositAmount = 100.0;
        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
            walletService.deposit(invalidWalletId, depositAmount)
        );
        verify(walletRepository, times(1)).findById(invalidWalletId);
        verify(transactionRepository, times(0)).save(any(Transaction.class));
    }

    @Test
    void testWithdrawWithSufficientFunds() {
        Double withdrawAmount = 50.0;
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        walletService.deposit(walletId, 100.0);

        Double newBalance = walletService.withdraw(walletId, withdrawAmount);

        assertEquals(50.0, newBalance);
        verify(walletRepository, times(2)).findById(walletId);
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

    @Test
    void testWithdrawNegativeAmount() {
        Double withdrawAmount = -150.0;
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        walletService.deposit(walletId, 100.0);

        assertThrows(WithdrawAmountMustBePositiveException.class, () ->
            walletService.withdraw(walletId, withdrawAmount)
        );
        verify(walletRepository, times(2)).findById(walletId);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testWithdrawWithInsufficientFunds() {
        Double withdrawAmount = 150.0;
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        walletService.deposit(walletId, 100.0);

        assertThrows(InsufficientFundsException.class, () ->
            walletService.withdraw(walletId, withdrawAmount)
        );
        verify(walletRepository, times(2)).findById(walletId);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testWithdrawWithInvalidWalletId() {
        Long invalidWalletId = 2L;
        Double withdrawAmount = 50.0;
        when(walletRepository.findById(invalidWalletId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
            walletService.withdraw(invalidWalletId, withdrawAmount)
        );
        verify(walletRepository, times(1)).findById(invalidWalletId);
        verify(transactionRepository, times(0)).save(any(Transaction.class));
    }

    @Test
    void testTransferSuccessful() {
        Long senderWalletId = 1L;
        Long recipientWalletId = 2L;
        Double transferAmount = 30.0;
        Wallet senderWallet = new Wallet();
        Wallet recipientWallet = new Wallet();

        when(walletRepository.findById(senderWalletId)).thenReturn(Optional.of(senderWallet));
        when(walletRepository.findById(recipientWalletId)).thenReturn(Optional.of(recipientWallet));

        walletService.deposit(senderWalletId, 100.0);
        Double newBalance = walletService.transfer(senderWalletId, recipientWalletId, transferAmount);

        assertEquals(70.0, newBalance);
        verify(walletRepository, times(2)).findById(senderWalletId);
        verify(walletRepository, times(1)).findById(recipientWalletId);
        verify(transactionRepository, times(3)).save(any(Transaction.class));
    }

    @Test
    void testTransferUserNotFoundForSender() {
        Long invalidsenderWalletId = 1L;
        Long recipientWalletId = 2L;
        Double transferAmount = 30.0;
        Wallet recipientWallet = new Wallet();

        when(walletRepository.findById(invalidsenderWalletId)).thenReturn(Optional.empty());
        when(walletRepository.findById(recipientWalletId)).thenReturn(Optional.of(recipientWallet));

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            walletService.transfer(invalidsenderWalletId, recipientWalletId, transferAmount);
        });

        assertEquals("Sender not found", exception.getMessage());
        verify(walletRepository, times(1)).findById(invalidsenderWalletId);
        verify(walletRepository, times(0)).findById(recipientWalletId);
        verify(transactionRepository, times(0)).save(any(Transaction.class));
    }

    @Test
    void testTransferUserNotFoundForRecipient() {
        Long senderWalletId = 1L;
        Long invalidrecipientWalletId = 2L;
        Double transferAmount = 30.0;
        Wallet senderWallet = new Wallet();

        when(walletRepository.findById(senderWalletId)).thenReturn(Optional.of(senderWallet));
        when(walletRepository.findById(invalidrecipientWalletId)).thenReturn(Optional.empty());

        walletService.deposit(senderWalletId, 100.0);
        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            walletService.transfer(senderWalletId, invalidrecipientWalletId, transferAmount);
        });

        assertEquals("Recipient not found", exception.getMessage());
        verify(walletRepository, times(2)).findById(senderWalletId);
        verify(walletRepository, times(1)).findById(invalidrecipientWalletId);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testTransferInsufficientFunds() {
        Long senderWalletId = 1L;
        Long recipientWalletId = 2L;
        Double transferAmount = 130.0;
        Wallet senderWallet = new Wallet();
        Wallet recipientWallet = new Wallet();

        when(walletRepository.findById(senderWalletId)).thenReturn(Optional.of(senderWallet));
        when(walletRepository.findById(recipientWalletId)).thenReturn(Optional.of(recipientWallet));

        walletService.deposit(senderWalletId, 100.0);
        Exception exception = assertThrows(InsufficientFundsException.class, () -> {
            walletService.transfer(senderWalletId, recipientWalletId, transferAmount);
        });

        assertEquals("Insufficient funds for transfer", exception.getMessage());
        verify(walletRepository, times(2)).findById(senderWalletId);
        verify(walletRepository, times(1)).findById(recipientWalletId);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testTransferAmountMustBePositive() {
        Long senderWalletId = 1L;
        Long recipientWalletId = 2L;
        Double transferAmount = -10.0;
        Wallet senderWallet = new Wallet();
        Wallet recipientWallet = new Wallet();

        when(walletRepository.findById(senderWalletId)).thenReturn(Optional.of(senderWallet));
        when(walletRepository.findById(recipientWalletId)).thenReturn(Optional.of(recipientWallet));

        walletService.deposit(senderWalletId, 100.0);
        Exception exception = assertThrows(TransferAmountMustBePositiveException.class, () -> {
            walletService.transfer(senderWalletId, recipientWalletId, transferAmount);
        });

        assertEquals("Transfer amount must be positive", exception.getMessage());
        verify(walletRepository, times(2)).findById(senderWalletId);
        verify(walletRepository, times(1)).findById(recipientWalletId);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testIsUserWalletOwnerWhenUserOwnsWallet() {
        Long walletId = 1L;
        Long userId = 10L;
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));

        boolean result = walletService.isUserWalletOwner(userId, walletId);

        assertTrue(result);
        verify(walletRepository, times(1)).findIdByUserId(userId);
    }

    @Test
    void testIsUserWalletOwnerWhenUserDoesNotOwnWallet() {
        Long userId = 1L;
        Long walletId = 20L;
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(10L));

        boolean result = walletService.isUserWalletOwner(userId, walletId);

        assertFalse(result);
        verify(walletRepository, times(1)).findIdByUserId(userId);
    }

    @Test
    void testIsUserWalletOwnerWhenUserNotFound() {
        Long userId = 1L;
        Long walletId = 10L;
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            walletService.isUserWalletOwner(userId, walletId);
        });

        assertEquals("User not found", exception.getMessage());
        verify(walletRepository, times(1)).findIdByUserId(userId);
    }
}
