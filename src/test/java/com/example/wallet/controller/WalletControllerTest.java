package com.example.wallet.controller;

import com.example.wallet.Exceptions.DepositAmountMustBePositiveException;
import com.example.wallet.Exceptions.InsufficientFundsException;
import com.example.wallet.Exceptions.UserNotFoundException;
import com.example.wallet.Exceptions.WithdrawAmountMustBePositiveException;
import com.example.wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class WalletControllerTest {

    @InjectMocks
    private WalletController walletController;

    @Mock
    private WalletService walletService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDepositWhenSuccessful() {
        String username = "testUser";
        Double amount = 100.0;
        when(walletService.deposit(username, amount)).thenReturn(amount);

        ResponseEntity<?> response = walletController.deposit(username, amount);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(amount, response.getBody());
    }

    @Test
    void testDepositWhenUserNotFoundException() {
        String username = "invalidUser";
        Double amount = 100.0;
        when(walletService.deposit(username, amount)).thenThrow(new UserNotFoundException("User not found"));

        ResponseEntity<?> response = walletController.deposit(username, amount);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred: User not found", response.getBody());
    }

    @Test
    void testDepositWhenDepositAmountIsNegative() {
        String username = "testUser";
        Double amount = 100.0;
        when(walletService.deposit(username, amount)).thenThrow(new DepositAmountMustBePositiveException("Deposit amount must be positive"));

        ResponseEntity<?> response = walletController.deposit(username, amount);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred: Deposit amount must be positive", response.getBody());
    }

    @Test
    void testWithdrawWhenSuccessful() {
        String username = "testUser";
        Double amount = 100.0;
        when(walletService.withdraw(username, amount)).thenReturn(amount);

        ResponseEntity<?> response = walletController.withdraw(username, amount);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(amount, response.getBody());
    }

    @Test
    void testWithdrawWhenUserNotFoundException() {
        String username = "invalidUser";
        Double amount = 100.0;
        when(walletService.withdraw(username, amount)).thenThrow(new UserNotFoundException("User not found"));

        ResponseEntity<?> response = walletController.withdraw(username, amount);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred: User not found", response.getBody());
    }

    @Test
    void testDepositWhenWithdrawAmountIsNegative() {
        String username = "testUser";
        Double amount = -100.0;
        when(walletService.withdraw(username, amount)).thenThrow(new WithdrawAmountMustBePositiveException("Withdraw amount must be positive"));

        ResponseEntity<?> response = walletController.withdraw(username, amount);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred: Withdraw amount must be positive", response.getBody());
    }

    @Test
    void testWithdrawWhenInsufficientFundsException() {
        String username = "testUser";
        Double amount = 50.0;
        when(walletService.withdraw(username, amount)).thenThrow(new InsufficientFundsException("Insufficient funds"));

        ResponseEntity<?> response = walletController.withdraw(username, amount);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred: Insufficient funds", response.getBody());
    }
}
