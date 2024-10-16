package com.example.wallet.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

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

import java.util.Optional;

public class WalletServiceTest {
    Long walletId;
    Wallet wallet;

    @InjectMocks
    private WalletService walletService;

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
    }

    @Test
    void testDeposit() {
        Double depositAmount = 100.0;
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        Double newBalance = walletService.deposit(walletId, depositAmount);

        assertEquals(depositAmount, newBalance);
        verify(walletRepository, times(1)).findById(walletId);
        verify(intraTransactionRepository, times(1)).save(any(IntraTransaction.class));
    }

    @Test
    void testDepositNegativeAmount() {
        Double depositAmount = -100.0;
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        assertThrows(DepositAmountMustBePositiveException.class, () ->
                walletService.deposit(walletId, depositAmount)
        );
        verify(walletRepository, times(1)).findById(walletId);
        verify(intraTransactionRepository, times(0)).save(any(IntraTransaction.class));
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
        verify(intraTransactionRepository, times(0)).save(any(IntraTransaction.class));
    }

    @Test
    void testWithdrawWithSufficientFunds() {
        Double withdrawAmount = 50.0;
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        walletService.deposit(walletId, 100.0);

        Double newBalance = walletService.withdraw(walletId, withdrawAmount);

        assertEquals(50.0, newBalance);
        verify(walletRepository, times(2)).findById(walletId);
        verify(intraTransactionRepository, times(2)).save(any(IntraTransaction.class));
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
        verify(intraTransactionRepository, times(1)).save(any(IntraTransaction.class));
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
        verify(intraTransactionRepository, times(1)).save(any(IntraTransaction.class));
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
        verify(intraTransactionRepository, times(0)).save(any(IntraTransaction.class));
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
        verify(intraTransactionRepository, times(1)).save(any(IntraTransaction.class));
        verify(interTransactionRepository, times(1)).save(any(InterTransaction.class));
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
        verify(interTransactionRepository, times(0)).save(any(InterTransaction.class));
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
        verify(intraTransactionRepository, times(1)).save(any(IntraTransaction.class));
        verify(interTransactionRepository, times(0)).save(any(InterTransaction.class));
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
        verify(intraTransactionRepository, times(1)).save(any(IntraTransaction.class));
        verify(interTransactionRepository, times(0)).save(any(InterTransaction.class));
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
        verify(intraTransactionRepository, times(1)).save(any(IntraTransaction.class));
        verify(interTransactionRepository, times(0)).save(any(InterTransaction.class));
    }

    @Test
    void testIsUserAuthorizedWhenUserOwnsWallet() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Long walletId = 1L;
        Long userId = 10L;

        User user = new User("testUser", "password");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(walletId));

        boolean result = walletService.isUserAuthorized(userId, walletId);

        assertTrue(result);
        verify(walletRepository, times(1)).findIdByUserId(userId);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testIsUserAuthorizedWhenUserDoesNotOwnWallet() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Long userId = 1L;
        Long walletId = 20L;

        User user = new User("testUser", "password");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.findIdByUserId(userId)).thenReturn(Optional.of(10L));

        boolean result = walletService.isUserAuthorized(userId, walletId);

        assertFalse(result);
        verify(walletRepository, times(1)).findIdByUserId(userId);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testIsUserAuthorizedWhenUserNotFound() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Long userId = 1L;
        Long walletId = 10L;

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            walletService.isUserAuthorized(userId, walletId);
        });

        assertEquals("User not found", exception.getMessage());
        verify(walletRepository, times(0)).findIdByUserId(userId);
    }
}
