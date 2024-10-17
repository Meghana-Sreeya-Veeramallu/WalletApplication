package com.example.wallet.controller;

import com.example.wallet.Exceptions.*;
import com.example.wallet.dto.TransferDto;
import com.example.wallet.dto.WalletDto;
import com.example.wallet.service.WalletService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WalletControllerTest {
    private Long userId;
    private Long walletId;

    @InjectMocks
    private WalletController walletController;

    @Mock
    private WalletService walletService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();
        objectMapper = new ObjectMapper();
        userId = 1L;
        walletId = 2L;
    }

    @Test
    void testDepositWhenSuccessful() throws Exception {
        Double amount = 100.0;
        WalletDto requestBody = new WalletDto(amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(walletService.deposit(userId, walletId, amount)).thenReturn(amount);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/wallets/{walletId}/deposit", userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        assertTrue(responseBody.contains("Amount deposited successfully"));
        assertTrue(responseBody.contains(amount.toString()));

        verify(walletService, times(1)).deposit(userId, walletId, amount);
    }

    @Test
    void testDepositWhenUserNotFoundException() throws Exception {
        Double amount = 100.0;
        WalletDto requestBody = new WalletDto(amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(walletService.deposit(userId, walletId, amount)).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/wallets/{walletId}/deposit", userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));

        verify(walletService, times(1)).deposit(userId, walletId, amount);
    }

    @Test
    void testDepositWhenWalletDoesNotBelongToOwnerException() throws Exception {
        Double amount = 100.0;
        WalletDto requestBody = new WalletDto(amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(walletService.deposit(userId, walletId, amount)).thenThrow(new UserNotAuthorizedException("Access denied: User is not authorized"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/wallets/{walletId}/deposit", userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Access denied: User is not authorized"));

        verify(walletService, times(1)).deposit(userId, walletId, amount);
    }

    @Test
    void testDepositWhenDepositAmountIsNegative() throws Exception {
        Double amount = -100.0;
        WalletDto requestBody = new WalletDto(amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(walletService.deposit(userId, walletId, amount)).thenThrow(new DepositAmountMustBePositiveException("Deposit amount must be positive"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/wallets/{walletId}/deposit", userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request: Deposit amount must be positive"));

        verify(walletService, times(1)).deposit(userId, walletId, amount);
    }

    @Test
    void testWithdrawWhenSuccessful() throws Exception {
        Double amount = 100.0;
        WalletDto requestBody = new WalletDto(amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(walletService.withdraw(userId, walletId, amount)).thenReturn(amount);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/wallets/{walletId}/withdrawal", userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isOk())
                .andReturn();;

        String responseBody = mvcResult.getResponse().getContentAsString();
        assertTrue(responseBody.contains("Amount withdrawn successfully"));
        assertTrue(responseBody.contains(amount.toString()));

        verify(walletService, times(1)).withdraw(userId, walletId, amount);
    }

    @Test
    void testWithdrawWhenUserNotFoundException() throws Exception {
        Double amount = 100.0;
        WalletDto requestBody = new WalletDto(amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(walletService.withdraw(userId, walletId, amount)).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/wallets/{walletId}/withdrawal", userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));

        verify(walletService, times(1)).withdraw(userId, walletId, amount);
    }

    @Test
    void testWithdrawWhenWalletDoesNotBelongToOwnerException() throws Exception {
        Double amount = 100.0;
        WalletDto requestBody = new WalletDto(amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(walletService.withdraw(userId, walletId, amount)).thenThrow(new UserNotAuthorizedException("Access denied: User is not authorized"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/wallets/{walletId}/withdrawal", userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Access denied: User is not authorized"));

        verify(walletService, times(1)).withdraw(userId, walletId, amount);
    }

    @Test
    void testWithdrawWhenWithdrawAmountIsNegative() throws Exception {
        Double amount = -100.0;
        WalletDto requestBody = new WalletDto(amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(walletService.withdraw(userId, walletId, amount)).thenThrow(new WithdrawAmountMustBePositiveException("Withdraw amount must be positive"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/wallets/{walletId}/withdrawal", userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request: Withdraw amount must be positive"));

        verify(walletService, times(1)).withdraw(userId, walletId, amount);
    }

    @Test
    void testWithdrawWhenInsufficientFundsException() throws Exception {
        Double amount = 50.0;
        WalletDto requestBody = new WalletDto(amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(walletService.withdraw(userId, walletId, amount)).thenThrow(new InsufficientFundsException("Insufficient funds"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/wallets/{walletId}/withdrawal", userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request: Insufficient funds"));

        verify(walletService, times(1)).withdraw(userId, walletId, amount);
    }

    @Test
    void testTransferWhenSuccessful() throws Exception {
        Long senderWalletId = 1L;
        Long recipientWalletId = 2L;
        Double amount = 30.0;
        TransferDto requestBody = new TransferDto(recipientWalletId, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(walletService.transfer(userId, senderWalletId, recipientWalletId, amount)).thenReturn(70.0);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/wallets/{senderWalletId}/transfer", userId, senderWalletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        assertTrue(responseBody.contains("Amount transferred successfully"));
        assertTrue(responseBody.contains(amount.toString()));

        verify(walletService, times(1)).transfer(userId, senderWalletId, recipientWalletId, amount);
    }

    @Test
    void testTransferWhenSenderNotFoundException() throws Exception {
        Long senderWalletId = 1L;
        Long recipientWalletId = 2L;
        Double amount = 30.0;
        TransferDto requestBody = new TransferDto(recipientWalletId, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(walletService.transfer(userId, senderWalletId, recipientWalletId, amount)).thenThrow(new UserNotFoundException("Sender not found"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/wallets/{senderWalletId}/transfer", userId, senderWalletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));

        verify(walletService, times(1)).transfer(userId, senderWalletId, recipientWalletId, amount);
    }

    @Test
    void testTransferWhenRecipientNotFoundException() throws Exception {
        Long senderWalletId = 1L;
        Long recipientWalletId = 2L;
        Double amount = 30.0;
        TransferDto requestBody = new TransferDto(recipientWalletId, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(walletService.transfer(userId, senderWalletId, recipientWalletId, amount)).thenThrow(new UserNotFoundException("Recipient not found"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/wallets/{senderWalletId}/transfer", userId, senderWalletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));

        verify(walletService, times(1)).transfer(userId, senderWalletId, recipientWalletId, amount);
    }

    @Test
    void testTransferWhenWalletDoesNotBelongToOwnerException() throws Exception {
        Long senderWalletId = 1L;
        Long recipientWalletId = 2L;
        Double amount = 30.0;
        TransferDto requestBody = new TransferDto(recipientWalletId, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(walletService.transfer(userId, senderWalletId, recipientWalletId, amount)).thenThrow(new UserNotAuthorizedException("Access denied: User is not authorized"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/wallets/{senderWalletId}/transfer", userId, senderWalletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Access denied: User is not authorized"));

        verify(walletService, times(1)).transfer(userId, senderWalletId, recipientWalletId, amount);
    }

    @Test
    void testTransferWhenInsufficientFundsException() throws Exception {
        Long senderWalletId = 1L;
        Long recipientWalletId = 2L;
        Double amount = 50.0;
        TransferDto requestBody = new TransferDto(recipientWalletId, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(walletService.transfer(userId, senderWalletId, recipientWalletId, amount)).thenThrow(new InsufficientFundsException("Insufficient funds"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/wallets/{senderWalletId}/transfer", userId, senderWalletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request: Insufficient funds"));

        verify(walletService, times(1)).transfer(userId, senderWalletId, recipientWalletId, amount);
    }

    @Test
    void testTransferWhenTransferAmountIsNegative() throws Exception {
        Long senderWalletId = 1L;
        Long recipientWalletId = 2L;
        Double amount = -30.0;
        TransferDto requestBody = new TransferDto(recipientWalletId, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(walletService.transfer(userId, senderWalletId, recipientWalletId, amount)).thenThrow(new TransferAmountMustBePositiveException("Transfer amount must be positive"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/wallets/{senderWalletId}/transfer", userId, senderWalletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request: Transfer amount must be positive"));

        verify(walletService, times(1)).transfer(userId, senderWalletId, recipientWalletId, amount);
    }
}
