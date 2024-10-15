package com.example.wallet.controller;

import com.example.wallet.Exceptions.*;
import com.example.wallet.model.Transaction;
import com.example.wallet.service.TransactionService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
        objectMapper = new ObjectMapper();
        userId = 1L;
        walletId = 2L;
    }

    @Test
    void testGetTransactionHistoryWhenSuccessful() throws Exception {
        List<Transaction> transactions = List.of(new Transaction(), new Transaction());

        when(transactionService.getTransactionHistory(userId)).thenReturn(transactions);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/wallets/{walletId}/transactions", userId, walletId))
                .andExpect(status().isOk())
                .andReturn();

        List<Transaction> responseBody = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(transactions.size(), responseBody.size());

        verify(transactionService, times(1)).getTransactionHistory(userId);
    }

    @Test
    void testGetTransactionHistoryWhenUserNotFoundException() throws Exception {
        when(transactionService.getTransactionHistory(userId)).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/wallets/{walletId}/transactions", userId, walletId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));

        verify(transactionService, times(1)).getTransactionHistory(userId);
    }

    @Test
    void testGetTransactionHistoryWhenOtherException() throws Exception {
        when(transactionService.getTransactionHistory(userId)).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/wallets/{walletId}/transactions", userId, walletId))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An error occurred: Unexpected error"));

        verify(transactionService, times(1)).getTransactionHistory(userId);
    }
}
