package com.example.wallet.controller;

import com.example.wallet.Enums.TransactionType;
import com.example.wallet.Exceptions.*;
import com.example.wallet.dto.TransactionDto;
import com.example.wallet.model.InterTransaction;
import com.example.wallet.model.IntraTransaction;
import com.example.wallet.model.Wallet;
import com.example.wallet.service.TransactionService;
import com.fasterxml.jackson.core.type.TypeReference;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TransactionControllerTest {
    private Long userId;
    private Long walletId;

    @InjectMocks
    private TransactionController transactionController;

    @Mock
    private TransactionService transactionService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).setControllerAdvice(new GlobalExceptionHandler()).build();
        objectMapper = new ObjectMapper();
        userId = 1L;
        walletId = 2L;
    }

    @Test
    void testDepositWhenSuccessful() throws Exception {
        Double amount = 100.0;
        TransactionDto requestBody = new TransactionDto("deposit", amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(transactionService.deposit(userId, walletId, amount)).thenReturn(amount);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/wallets/{walletId}/transactions", userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        assertTrue(responseBody.contains("Transaction successful: deposit"));
        assertTrue(responseBody.contains(amount.toString()));

        verify(transactionService, times(1)).deposit(userId, walletId, amount);
    }

    @Test
    void testDepositWhenTransactionTypeIsNull() throws Exception {
        Double amount = 30.0;
        TransactionDto requestBody = new TransactionDto(null, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/wallets/{senderWalletId}/transactions", userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Transaction type is required and cannot be null"));

        verify(transactionService, times(0)).deposit(userId, walletId, amount);
    }

    @Test
    void testDepositWhenAmountIsNull() throws Exception {
        TransactionDto requestBody = new TransactionDto("deposit", null);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/wallets/{senderWalletId}/transactions", userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Amount is required and cannot be null"));

        verify(transactionService, times(0)).deposit(userId, walletId, null);
    }

    @Test
    void testDepositWhenUserNotFoundException() throws Exception {
        Double amount = 100.0;
        TransactionDto requestBody = new TransactionDto("deposit", amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(transactionService.deposit(userId, walletId, amount)).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/wallets/{walletId}/transactions", userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));

        verify(transactionService, times(1)).deposit(userId, walletId, amount);
    }

    @Test
    void testDepositWhenWalletDoesNotBelongToOwnerException() throws Exception {
        Double amount = 100.0;
        TransactionDto requestBody = new TransactionDto("deposit", amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(transactionService.deposit(userId, walletId, amount)).thenThrow(new UserNotAuthorizedException("Access denied: User is not authorized"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/wallets/{walletId}/transactions", userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Access denied: User is not authorized"));

        verify(transactionService, times(1)).deposit(userId, walletId, amount);
    }

    @Test
    void testDepositWhenDepositAmountIsNegative() throws Exception {
        Double amount = -100.0;
        TransactionDto requestBody = new TransactionDto("deposit", amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(transactionService.deposit(userId, walletId, amount)).thenThrow(new DepositAmountMustBePositiveException("Deposit amount must be positive"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/wallets/{walletId}/transactions", userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request: Deposit amount must be positive"));

        verify(transactionService, times(1)).deposit(userId, walletId, amount);
    }

    @Test
    void testWithdrawWhenSuccessful() throws Exception {
        Double amount = 100.0;
        TransactionDto requestBody = new TransactionDto("withdrawal", amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(transactionService.withdraw(userId, walletId, amount)).thenReturn(amount);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/wallets/{walletId}/transactions", userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isOk())
                .andReturn();;

        String responseBody = mvcResult.getResponse().getContentAsString();
        assertTrue(responseBody.contains("Transaction successful: withdrawal"));
        assertTrue(responseBody.contains(amount.toString()));

        verify(transactionService, times(1)).withdraw(userId, walletId, amount);
    }

    @Test
    void testWithdrawWhenTransactionTypeIsNull() throws Exception {
        Double amount = 30.0;
        TransactionDto requestBody = new TransactionDto(null, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/wallets/{senderWalletId}/transactions", userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Transaction type is required and cannot be null"));

        verify(transactionService, times(0)).withdraw(userId, walletId, amount);
    }

    @Test
    void testWithdrawWhenAmountIsNull() throws Exception {
        TransactionDto requestBody = new TransactionDto("withdrawal", null);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/wallets/{senderWalletId}/transactions", userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Amount is required and cannot be null"));

        verify(transactionService, times(0)).withdraw(userId, walletId, null);
    }

    @Test
    void testWithdrawWhenUserNotFoundException() throws Exception {
        Double amount = 100.0;
        TransactionDto requestBody = new TransactionDto("withdrawal", amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(transactionService.withdraw(userId, walletId, amount)).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/wallets/{walletId}/transactions", userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));

        verify(transactionService, times(1)).withdraw(userId, walletId, amount);
    }

    @Test
    void testWithdrawWhenWalletDoesNotBelongToOwnerException() throws Exception {
        Double amount = 100.0;
        TransactionDto requestBody = new TransactionDto("withdrawal", amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(transactionService.withdraw(userId, walletId, amount)).thenThrow(new UserNotAuthorizedException("Access denied: User is not authorized"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/wallets/{walletId}/transactions", userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Access denied: User is not authorized"));

        verify(transactionService, times(1)).withdraw(userId, walletId, amount);
    }

    @Test
    void testWithdrawWhenWithdrawAmountIsNegative() throws Exception {
        Double amount = -100.0;
        TransactionDto requestBody = new TransactionDto("withdrawal", amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(transactionService.withdraw(userId, walletId, amount)).thenThrow(new WithdrawAmountMustBePositiveException("Withdraw amount must be positive"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/wallets/{walletId}/transactions", userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request: Withdraw amount must be positive"));

        verify(transactionService, times(1)).withdraw(userId, walletId, amount);
    }

    @Test
    void testWithdrawWhenInsufficientFundsException() throws Exception {
        Double amount = 50.0;
        TransactionDto requestBody = new TransactionDto("withdrawal", amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(transactionService.withdraw(userId, walletId, amount)).thenThrow(new InsufficientFundsException("Insufficient funds"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/wallets/{walletId}/transactions", userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request: Insufficient funds"));

        verify(transactionService, times(1)).withdraw(userId, walletId, amount);
    }

    @Test
    void testTransferWhenSuccessful() throws Exception {
        Long senderWalletId = 1L;
        Long recipientWalletId = 2L;
        Double amount = 30.0;
        TransactionDto requestBody = new TransactionDto("transfer", recipientWalletId, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(transactionService.transfer(userId, senderWalletId, recipientWalletId, amount)).thenReturn(70.0);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/wallets/{senderWalletId}/transactions", userId, senderWalletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        assertTrue(responseBody.contains("Transaction successful: transfer"));
        assertTrue(responseBody.contains(amount.toString()));

        verify(transactionService, times(1)).transfer(userId, senderWalletId, recipientWalletId, amount);
    }

    @Test
    void testTransferWhenTransactionTypeIsNull() throws Exception {
        Long senderWalletId = 1L;
        Long recipientWalletId = 2L;
        Double amount = 30.0;
        TransactionDto requestBody = new TransactionDto(null, recipientWalletId, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/wallets/{senderWalletId}/transactions", userId, senderWalletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Transaction type is required and cannot be null"));

        verify(transactionService, times(0)).transfer(userId, senderWalletId, recipientWalletId, amount);
    }

    @Test
    void testTransferWhenRecipientWalletIdIsNull() throws Exception {
        Long senderWalletId = 1L;
        Double amount = 30.0;
        TransactionDto requestBody = new TransactionDto("transfer", null, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/wallets/{senderWalletId}/transactions", userId, senderWalletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Recipient wallet ID is required for transfers"));

        verify(transactionService, times(0)).transfer(userId, senderWalletId, null, amount);
    }

    @Test
    void testTransferWhenAmountIsNull() throws Exception {
        Long senderWalletId = 1L;
        Long recipientWalletId = 2L;
        TransactionDto requestBody = new TransactionDto("transfer", recipientWalletId, null);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/wallets/{senderWalletId}/transactions", userId, senderWalletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Amount is required and cannot be null"));

        verify(transactionService, times(0)).transfer(userId, senderWalletId, recipientWalletId, null);
    }

    @Test
    void testTransferWhenSenderNotFoundException() throws Exception {
        Long senderWalletId = 1L;
        Long recipientWalletId = 2L;
        Double amount = 30.0;
        TransactionDto requestBody = new TransactionDto("transfer", recipientWalletId, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(transactionService.transfer(userId, senderWalletId, recipientWalletId, amount)).thenThrow(new UserNotFoundException("Sender not found"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/wallets/{senderWalletId}/transactions", userId, senderWalletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));

        verify(transactionService, times(1)).transfer(userId, senderWalletId, recipientWalletId, amount);
    }

    @Test
    void testTransferWhenRecipientNotFoundException() throws Exception {
        Long senderWalletId = 1L;
        Long recipientWalletId = 2L;
        Double amount = 30.0;
        TransactionDto requestBody = new TransactionDto("transfer", recipientWalletId, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(transactionService.transfer(userId, senderWalletId, recipientWalletId, amount)).thenThrow(new UserNotFoundException("Recipient not found"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/wallets/{senderWalletId}/transactions", userId, senderWalletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));

        verify(transactionService, times(1)).transfer(userId, senderWalletId, recipientWalletId, amount);
    }

    @Test
    void testTransferWhenWalletDoesNotBelongToOwnerException() throws Exception {
        Long senderWalletId = 1L;
        Long recipientWalletId = 2L;
        Double amount = 30.0;
        TransactionDto requestBody = new TransactionDto("transfer", recipientWalletId, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(transactionService.transfer(userId, senderWalletId, recipientWalletId, amount)).thenThrow(new UserNotAuthorizedException("Access denied: User is not authorized"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/wallets/{senderWalletId}/transactions", userId, senderWalletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Access denied: User is not authorized"));

        verify(transactionService, times(1)).transfer(userId, senderWalletId, recipientWalletId, amount);
    }

    @Test
    void testTransferWhenInsufficientFundsException() throws Exception {
        Long senderWalletId = 1L;
        Long recipientWalletId = 2L;
        Double amount = 50.0;
        TransactionDto requestBody = new TransactionDto("transfer", recipientWalletId, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(transactionService.transfer(userId, senderWalletId, recipientWalletId, amount)).thenThrow(new InsufficientFundsException("Insufficient funds"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/wallets/{senderWalletId}/transactions", userId, senderWalletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request: Insufficient funds"));

        verify(transactionService, times(1)).transfer(userId, senderWalletId, recipientWalletId, amount);
    }

    @Test
    void testTransferWhenTransferAmountIsNegative() throws Exception {
        Long senderWalletId = 1L;
        Long recipientWalletId = 2L;
        Double amount = -30.0;
        TransactionDto requestBody = new TransactionDto("transfer", recipientWalletId, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(transactionService.transfer(userId, senderWalletId, recipientWalletId, amount)).thenThrow(new TransferAmountMustBePositiveException("Transfer amount must be positive"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/wallets/{senderWalletId}/transactions", userId, senderWalletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request: Transfer amount must be positive"));

        verify(transactionService, times(1)).transfer(userId, senderWalletId, recipientWalletId, amount);
    }

    @Test
    void testInvalidTransactionTypeException() throws Exception {
        TransactionDto requestBody = new TransactionDto("random", 30.0);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/wallets/{senderWalletId}/transactions", userId, walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid transaction type: random"));

        verify(transactionService, times(0)).withdraw(userId, walletId, null);
    }

    @Test
    void testGetTransactionHistoryWhenSuccessful() throws Exception {
        List<Object> transactions = new ArrayList<>();
        transactions.add(new IntraTransaction(new Wallet(), TransactionType.WITHDRAWAL, 10.0));
        transactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 100.0));

        when(transactionService.getTransactionHistory(userId, walletId, null, null, null)).thenReturn(transactions);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/wallets/{walletId}/transactions", userId, walletId))
                .andExpect(status().isOk())
                .andReturn();

        List<Object> responseBody = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(transactions.size(), responseBody.size());
        verify(transactionService, times(1)).getTransactionHistory(userId, walletId, null, null, null);
    }

    @Test
    void testGetTransactionHistoryWhenUserNotFoundException() throws Exception {
        when(transactionService.getTransactionHistory(userId, walletId, null, null, null)).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/wallets/{walletId}/transactions", userId, walletId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));

        verify(transactionService, times(1)).getTransactionHistory(userId, walletId, null, null, null);
    }

    @Test
    void testGetTransactionHistoryWhenWalletDoesNotBelongToUserException() throws Exception {
        when(transactionService.getTransactionHistory(userId, walletId, null, null, null)).thenThrow(new UserNotAuthorizedException("Access denied: User is not authorized"));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/wallets/{walletId}/transactions", userId, walletId))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Access denied: User is not authorized"));

        verify(transactionService, times(1)).getTransactionHistory(userId, walletId, null, null, null);
    }

    @Test
    void testGetTransactionHistoryWhenOtherException() throws Exception {
        when(transactionService.getTransactionHistory(userId, walletId, null, null, null)).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/wallets/{walletId}/transactions", userId, walletId))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An error occurred: Unexpected error"));

        verify(transactionService, times(1)).getTransactionHistory(userId, walletId, null, null, null);
    }

    @Test
    void testGetTransactionHistoryWhenSortByTimestampAscending() throws Exception {
        List<Object> transactions = new ArrayList<>();
        transactions.add(new IntraTransaction(new Wallet(), TransactionType.WITHDRAWAL, 10.0));
        transactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 100.0));

        when(transactionService.getTransactionHistory(userId, walletId, "timestamp", "ASC", null)).thenReturn(transactions);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/wallets/{walletId}/transactions?sortBy=timestamp&sortOrder=ASC", userId, walletId))
                .andExpect(status().isOk())
                .andReturn();

        List<Object> responseBody = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(transactions.size(), responseBody.size());
        verify(transactionService, times(1)).getTransactionHistory(userId, walletId, "timestamp", "ASC", null);
    }

    @Test
    void testGetTransactionHistoryWhenSortByTimestampDescending() throws Exception {
        List<Object> transactions = new ArrayList<>();
        transactions.add(new IntraTransaction(new Wallet(), TransactionType.WITHDRAWAL, 10.0));
        transactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 100.0));

        when(transactionService.getTransactionHistory(userId, walletId, "timestamp", "DESC", null)).thenReturn(transactions);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/wallets/{walletId}/transactions?sortBy=timestamp&sortOrder=DESC", userId, walletId))
                .andExpect(status().isOk())
                .andReturn();

        List<Object> responseBody = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(transactions.size(), responseBody.size());
        verify(transactionService, times(1)).getTransactionHistory(userId, walletId, "timestamp", "DESC", null);
    }

    @Test
    void testGetTransactionHistoryWhenTypeDeposit() throws Exception {
        List<Object> transactions = new ArrayList<>();
        transactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 10.0));
        transactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 70.0));

        when(transactionService.getTransactionHistory(userId, walletId, null, null, "DEPOSIT")).thenReturn(transactions);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/wallets/{walletId}/transactions?transactionType=DEPOSIT", userId, walletId))
                .andExpect(status().isOk())
                .andReturn();

        List<Object> responseBody = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(transactions.size(), responseBody.size());
        verify(transactionService, times(1)).getTransactionHistory(userId, walletId, null, null, "DEPOSIT");
    }

    @Test
    void testGetTransactionHistoryWhenTypeWithdrawal() throws Exception {
        List<Object> transactions = new ArrayList<>();
        transactions.add(new IntraTransaction(new Wallet(), TransactionType.WITHDRAWAL, 10.0));
        transactions.add(new IntraTransaction(new Wallet(), TransactionType.WITHDRAWAL, 70.0));

        when(transactionService.getTransactionHistory(userId, walletId, null, null, "WITHDRAWAL")).thenReturn(transactions);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/wallets/{walletId}/transactions?transactionType=WITHDRAWAL", userId, walletId))
                .andExpect(status().isOk())
                .andReturn();

        List<Object> responseBody = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(transactions.size(), responseBody.size());
        verify(transactionService, times(1)).getTransactionHistory(userId, walletId, null, null, "WITHDRAWAL");
    }

    @Test
    void testGetTransactionHistoryWhenTypeTransfer() throws Exception {
        List<Object> transactions = new ArrayList<>();
        transactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 10.0));
        transactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 70.0));

        when(transactionService.getTransactionHistory(userId, walletId, null, null, "TRANSFER")).thenReturn(transactions);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/wallets/{walletId}/transactions?transactionType=TRANSFER", userId, walletId))
                .andExpect(status().isOk())
                .andReturn();

        List<Object> responseBody = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(transactions.size(), responseBody.size());
        verify(transactionService, times(1)).getTransactionHistory(userId, walletId, null, null, "TRANSFER");
    }

    @Test
    void testGetTransactionHistoryWhenSortByTimestampAscendingAndTypeTransfer() throws Exception {
        List<Object> transactions = new ArrayList<>();
        transactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 10.0));
        transactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 70.0));

        when(transactionService.getTransactionHistory(userId, walletId, "timestamp", "ASC", "TRANSFER")).thenReturn(transactions);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/wallets/{walletId}/transactions?sortBy=timestamp&sortOrder=ASC&transactionType=TRANSFER", userId, walletId))
                .andExpect(status().isOk())
                .andReturn();

        List<Object> responseBody = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(transactions.size(), responseBody.size());
        verify(transactionService, times(1)).getTransactionHistory(userId, walletId, "timestamp", "ASC", "TRANSFER");
    }

    @Test
    void testGetTransactionHistoryWhenSortByAmountDescendingAndTypeDeposit() throws Exception {
        List<Object> transactions = new ArrayList<>();
        transactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));
        transactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 200.0));

        when(transactionService.getTransactionHistory(userId, walletId, "amount", "DESC", "DEPOSIT")).thenReturn(transactions);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/wallets/{walletId}/transactions?sortBy=amount&sortOrder=DESC&transactionType=DEPOSIT", userId, walletId))
                .andExpect(status().isOk())
                .andReturn();

        List<Object> responseBody = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(transactions.size(), responseBody.size());
        verify(transactionService, times(1)).getTransactionHistory(userId, walletId, "amount", "DESC", "DEPOSIT");
    }

    @Test
    void testGetTransactionHistoryWhenSortByAmountAndDescending() throws Exception {
        List<Object> transactions = new ArrayList<>();
        transactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 200.0));
        transactions.add(new IntraTransaction(new Wallet(), TransactionType.WITHDRAWAL, 100.0));

        when(transactionService.getTransactionHistory(userId, walletId, "amount", "DESC", null)).thenReturn(transactions);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/wallets/{walletId}/transactions?sortBy=amount&sortOrder=DESC", userId, walletId))
                .andExpect(status().isOk())
                .andReturn();

        List<Object> responseBody = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(transactions.size(), responseBody.size());
        verify(transactionService, times(1)).getTransactionHistory(userId, walletId, "amount", "DESC", null);
    }

    @Test
    void testGetTransactionHistoryWhenSortByAmountAndTimestampDescending() throws Exception {
        List<Object> transactions = new ArrayList<>();
        transactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 200.0));
        transactions.add(new IntraTransaction(new Wallet(), TransactionType.WITHDRAWAL, 100.0));

        when(transactionService.getTransactionHistory(userId, walletId, "amount,timestamp", "DESC,DESC", null)).thenReturn(transactions);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/wallets/{walletId}/transactions?sortBy=amount,timestamp&sortOrder=DESC,DESC", userId, walletId))
                .andExpect(status().isOk())
                .andReturn();

        List<Object> responseBody = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(transactions.size(), responseBody.size());
        verify(transactionService, times(1)).getTransactionHistory(userId, walletId, "amount,timestamp", "DESC,DESC", null);
    }

    @Test
    void testGetTransactionHistoryWhenSortByInvalidField() throws Exception {
        when(transactionService.getTransactionHistory(userId, walletId, "amounts", "DESC", null)).thenThrow(new IllegalArgumentException("Invalid sort field: amounts"));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/wallets/{walletId}/transactions?sortBy=amounts&sortOrder=DESC", userId, walletId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request: Invalid sort field: amounts"));

        verify(transactionService, times(1)).getTransactionHistory(userId, walletId, "amounts", "DESC", null);
    }

    @Test
    void testGetTransactionHistoryWhenSortOrderInvalid() throws Exception {
        when(transactionService.getTransactionHistory(userId, walletId, "amount", "DES", null)).thenThrow(new IllegalArgumentException("Invalid sort order: DES"));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/wallets/{walletId}/transactions?sortBy=amount&sortOrder=DES", userId, walletId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request: Invalid sort order: DES"));

        verify(transactionService, times(1)).getTransactionHistory(userId, walletId, "amount", "DES", null);
    }

    @Test
    void testGetTransactionHistoryWhenSortOrderLengthIsLessThanSortByLength() throws Exception {
        List<Object> transactions = new ArrayList<>();
        transactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 200.0));
        transactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));

        when(transactionService.getTransactionHistory(userId, walletId, "amount,timestamp", "DESC", null)).thenReturn(transactions);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/wallets/{walletId}/transactions?sortBy=amount,timestamp&sortOrder=DESC", userId, walletId))
                .andExpect(status().isOk())
                .andReturn();

        List<Object> responseBody = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(transactions.size(), responseBody.size());
        verify(transactionService, times(1)).getTransactionHistory(userId, walletId, "amount,timestamp", "DESC", null);
    }

    @Test
    void testGetTransactionHistoryWhenSortOrderLengthIsGreaterThanSortByLength() throws Exception {
        when(transactionService.getTransactionHistory(userId, walletId, "amount", "DESC,ASC", null)).thenThrow(new IllegalArgumentException("The number of sort fields must be greater than or equal to the number of sort orders"));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/wallets/{walletId}/transactions?sortBy=amount&sortOrder=DESC,ASC", userId, walletId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request: The number of sort fields must be greater than or equal to the number of sort orders"));

        verify(transactionService, times(1)).getTransactionHistory(userId, walletId, "amount", "DESC,ASC", null);
    }

    @Test
    void testGetTransactionHistoryWhenSortByUpperCaseAmount() throws Exception {
        List<Object> transactions = new ArrayList<>();
        transactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 200.0));
        transactions.add(new IntraTransaction(new Wallet(), TransactionType.WITHDRAWAL, 100.0));

        when(transactionService.getTransactionHistory(userId, walletId, "AMOUNT", "DESC", null)).thenReturn(transactions);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/wallets/{walletId}/transactions?sortBy=AMOUNT&sortOrder=DESC", userId, walletId))
                .andExpect(status().isOk())
                .andReturn();

        List<Object> responseBody = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(transactions.size(), responseBody.size());
        verify(transactionService, times(1)).getTransactionHistory(userId, walletId, "AMOUNT", "DESC", null);
    }

    @Test
    void testGetTransactionHistoryWhenSortOrderLowerCaseDesc() throws Exception {
        List<Object> transactions = new ArrayList<>();
        transactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 200.0));
        transactions.add(new IntraTransaction(new Wallet(), TransactionType.WITHDRAWAL, 100.0));

        when(transactionService.getTransactionHistory(userId, walletId, "amount", "desc", null)).thenReturn(transactions);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/wallets/{walletId}/transactions?sortBy=amount&sortOrder=desc", userId, walletId))
                .andExpect(status().isOk())
                .andReturn();

        List<Object> responseBody = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(transactions.size(), responseBody.size());
        verify(transactionService, times(1)).getTransactionHistory(userId, walletId, "amount", "desc", null);
    }

    @Test
    void testGetTransactionHistoryWithTypeDepositAndTransfer() throws Exception {
        Long walletId = 2L;

        List<Object> transactions = new ArrayList<>();
        transactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));
        transactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 200.0));
        transactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 100.0));

        when(transactionService.getTransactionHistory(userId, walletId, null, null, "DEPOSIT,TRANSFER")).thenReturn(transactions);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/wallets/{walletId}/transactions?transactionType=DEPOSIT,TRANSFER", userId, walletId))
                .andExpect(status().isOk())
                .andReturn();

        List<Object> responseBody = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(transactions.size(), responseBody.size());
        verify(transactionService, times(1)).getTransactionHistory(userId, walletId, null, null, "DEPOSIT,TRANSFER");

    }

    @Test
    void testGetTransactionHistoryWithTransferTypeDepositTransferAndWithdrawal() throws Exception {
        Long walletId = 2L;

        List<Object> transactions = new ArrayList<>();
        transactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));
        transactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 200.0));
        transactions.add(new IntraTransaction(new Wallet(), TransactionType.WITHDRAWAL, 50.0));
        transactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 100.0));

        when(transactionService.getTransactionHistory(userId, walletId, null, null, "DEPOSIT,TRANSFER,WITHDRAWAL")).thenReturn(transactions);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/wallets/{walletId}/transactions?transactionType=DEPOSIT,TRANSFER,WITHDRAWAL", userId, walletId))
                .andExpect(status().isOk())
                .andReturn();

        List<Object> responseBody = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(transactions.size(), responseBody.size());
        verify(transactionService, times(1)).getTransactionHistory(userId, walletId, null, null, "DEPOSIT,TRANSFER,WITHDRAWAL");
    }

    @Test
    void testGetTransactionHistoryWithTransferTypeDepositAndTransferLowerCase() throws Exception {
        Long walletId = 2L;

        List<Object> transactions = new ArrayList<>();
        transactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));
        transactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 200.0));
        transactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 150.0));

        when(transactionService.getTransactionHistory(userId, walletId, null, null, "DEPOSIT,Transfer")).thenReturn(transactions);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/wallets/{walletId}/transactions?transactionType=DEPOSIT,Transfer", userId, walletId))
                .andExpect(status().isOk())
                .andReturn();

        List<Object> responseBody = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(3, responseBody.size());
        verify(transactionService, times(1)).getTransactionHistory(userId, walletId, null, null, "DEPOSIT,Transfer");
    }

    @Test
    void testGetTransactionHistoryWithTransferTypeInvalid() throws Exception {
        Long walletId = 2L;

        when(transactionService.getTransactionHistory(userId, walletId, null, null, "DEPOSIT,Transf")).thenThrow(new IllegalArgumentException("Invalid transaction type: Transf"));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/wallets/{walletId}/transactions?transactionType=DEPOSIT,Transf", userId, walletId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request: Invalid transaction type: Transf"));

        verify(transactionService, times(1)).getTransactionHistory(userId, walletId, null, null, "DEPOSIT,Transf");
    }
}
