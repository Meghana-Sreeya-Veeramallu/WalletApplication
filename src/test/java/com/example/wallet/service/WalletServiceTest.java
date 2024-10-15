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
    Long userId;
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
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testDepositNegativeAmount() {
        Double depositAmount = -100.0;
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        assertThrows(DepositAmountMustBePositiveException.class, () ->
                walletService.deposit(userId, depositAmount)
        );
        verify(walletRepository, times(1)).findByUserId(userId);
        verify(transactionRepository, times(0)).save(any(Transaction.class));
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
        verify(transactionRepository, times(0)).save(any(Transaction.class));
    }

    @Test
    void testWithdrawWithSufficientFunds() {
        Double withdrawAmount = 50.0;
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        walletService.deposit(userId, 100.0);

        Double newBalance = walletService.withdraw(userId, withdrawAmount);

        assertEquals(50.0, newBalance);
        verify(walletRepository, times(2)).findByUserId(userId);
        verify(transactionRepository, times(2)).save(any(Transaction.class));
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
        verify(transactionRepository, times(1)).save(any(Transaction.class));
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
        verify(transactionRepository, times(1)).save(any(Transaction.class));
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
        verify(transactionRepository, times(0)).save(any(Transaction.class));
    }

    @Test
    void testTransferSuccessful() {
        Long senderId = 1L;
        Long recipientId = 2L;
        Double transferAmount = 30.0;
        Wallet senderWallet = new Wallet();
        Wallet recipientWallet = new Wallet();

        when(walletRepository.findByUserId(senderId)).thenReturn(Optional.of(senderWallet));
        when(walletRepository.findByUserId(recipientId)).thenReturn(Optional.of(recipientWallet));

        walletService.deposit(senderId, 100.0);
        Double newBalance = walletService.transfer(senderId, recipientId, transferAmount);

        assertEquals(70.0, newBalance);
        verify(walletRepository, times(2)).findByUserId(senderId);
        verify(walletRepository, times(1)).findByUserId(recipientId);
        verify(transactionRepository, times(3)).save(any(Transaction.class));
    }

    @Test
    void testTransferUserNotFoundForSender() {
        Long invalidSenderId = 1L;
        Long recipientId = 2L;
        Double transferAmount = 30.0;
        Wallet recipientWallet = new Wallet();

        when(walletRepository.findByUserId(invalidSenderId)).thenReturn(Optional.empty());
        when(walletRepository.findByUserId(recipientId)).thenReturn(Optional.of(recipientWallet));

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            walletService.transfer(invalidSenderId, recipientId, transferAmount);
        });

        assertEquals("Sender not found", exception.getMessage());
        verify(walletRepository, times(1)).findByUserId(invalidSenderId);
        verify(walletRepository, times(0)).findByUserId(recipientId);
        verify(transactionRepository, times(0)).save(any(Transaction.class));
    }

    @Test
    void testTransferUserNotFoundForRecipient() {
        Long senderId = 1L;
        Long invalidRecipientId = 2L;
        Double transferAmount = 30.0;
        Wallet senderWallet = new Wallet();

        when(walletRepository.findByUserId(senderId)).thenReturn(java.util.Optional.of(senderWallet));
        when(walletRepository.findByUserId(invalidRecipientId)).thenReturn(java.util.Optional.empty());

        walletService.deposit(senderId, 100.0);
        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            walletService.transfer(senderId, invalidRecipientId, transferAmount);
        });

        assertEquals("Recipient not found", exception.getMessage());
        verify(walletRepository, times(2)).findByUserId(senderId);
        verify(walletRepository, times(1)).findByUserId(invalidRecipientId);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testTransferInsufficientFunds() {
        Long senderId = 1L;
        Long recipientId = 2L;
        Double transferAmount = 130.0;
        Wallet senderWallet = new Wallet();
        Wallet recipientWallet = new Wallet();

        when(walletRepository.findByUserId(senderId)).thenReturn(java.util.Optional.of(senderWallet));
        when(walletRepository.findByUserId(recipientId)).thenReturn(java.util.Optional.of(recipientWallet));

        walletService.deposit(senderId, 100.0);
        Exception exception = assertThrows(InsufficientFundsException.class, () -> {
            walletService.transfer(senderId, recipientId, transferAmount);
        });

        assertEquals("Insufficient funds for transfer", exception.getMessage());
        verify(walletRepository, times(2)).findByUserId(senderId);
        verify(walletRepository, times(1)).findByUserId(recipientId);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testTransferAmountMustBePositive() {
        Long senderId = 1L;
        Long recipientId = 2L;
        Double transferAmount = -10.0;
        Wallet senderWallet = new Wallet();
        Wallet recipientWallet = new Wallet();

        when(walletRepository.findByUserId(senderId)).thenReturn(java.util.Optional.of(senderWallet));
        when(walletRepository.findByUserId(recipientId)).thenReturn(java.util.Optional.of(recipientWallet));

        walletService.deposit(senderId, 100.0);
        Exception exception = assertThrows(TransferAmountMustBePositiveException.class, () -> {
            walletService.transfer(senderId, recipientId, transferAmount);
        });

        assertEquals("Transfer amount must be positive", exception.getMessage());
        verify(walletRepository, times(2)).findByUserId(senderId);
        verify(walletRepository, times(1)).findByUserId(recipientId);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }
}
