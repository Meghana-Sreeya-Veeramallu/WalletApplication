package com.example.wallet.controller;

import com.example.wallet.Enums.SortOrder;
import com.example.wallet.Enums.TransactionType;
import com.example.wallet.Exceptions.*;
import com.example.wallet.model.InterTransaction;
import com.example.wallet.model.IntraTransaction;
import com.example.wallet.model.Wallet;
import com.example.wallet.service.TransactionService;
import com.example.wallet.service.WalletService;
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

import java.util.ArrayList;
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
    @Mock
    private WalletService walletService;

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
        List<Object> transactions = new ArrayList<>();
        transactions.add(new IntraTransaction(new Wallet(), TransactionType.WITHDRAWAL, 10.0));
        transactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 100.0));

        when(walletService.isUserAuthorized(userId, walletId)).thenReturn(true);
        when(transactionService.getTransactionHistory(walletId, null, null)).thenReturn(transactions);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/wallets/{walletId}/transactions", userId, walletId))
                .andExpect(status().isOk())
                .andReturn();

        List<Object> responseBody = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(transactions.size(), responseBody.size());

        verify(transactionService, times(1)).getTransactionHistory(walletId, null, null);
    }

    @Test
    void testGetTransactionHistoryWhenUserNotFoundException() throws Exception {
        when(walletService.isUserAuthorized(userId, walletId)).thenReturn(true);
        when(transactionService.getTransactionHistory(walletId, null, null)).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/wallets/{walletId}/transactions", userId, walletId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));

        verify(transactionService, times(1)).getTransactionHistory(walletId, null, null);

    }

    @Test
    void testGetTransactionHistoryWhenWalletDoesNotBelongToUserException() throws Exception {
        List<Object> transactions = new ArrayList<>();
        transactions.add(new IntraTransaction());
        transactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 100.0));

        when(walletService.isUserAuthorized(userId, walletId)).thenReturn(false);
        when(transactionService.getTransactionHistory(walletId, null, null)).thenReturn(transactions);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/wallets/{walletId}/transactions", userId, walletId))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Access denied: User is not authorized"));

        verify(transactionService, times(0)).getTransactionHistory(walletId, null, null);

    }

    @Test
    void testGetTransactionHistoryWhenOtherException() throws Exception {
        when(walletService.isUserAuthorized(userId, walletId)).thenReturn(true);
        when(transactionService.getTransactionHistory(walletId, null, null)).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/wallets/{walletId}/transactions", userId, walletId))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An error occurred: Unexpected error"));

        verify(transactionService, times(1)).getTransactionHistory(walletId, null, null);
    }

    @Test
    void testGetTransactionHistoryWhenSortAscending() throws Exception {
        List<Object> transactions = new ArrayList<>();
        transactions.add(new IntraTransaction(new Wallet(), TransactionType.WITHDRAWAL, 10.0));
        transactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 100.0));

        when(walletService.isUserAuthorized(userId, walletId)).thenReturn(true);
        when(transactionService.getTransactionHistory(walletId, SortOrder.ASC, null)).thenReturn(transactions);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/wallets/{walletId}/transactions?sortOrder=ASC", userId, walletId))
                .andExpect(status().isOk())
                .andReturn();

        List<Object> responseBody = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(transactions.size(), responseBody.size());

        verify(transactionService, times(1)).getTransactionHistory(walletId, SortOrder.ASC, null);
    }

    @Test
    void testGetTransactionHistoryWhenSortDescending() throws Exception {
        List<Object> transactions = new ArrayList<>();
        transactions.add(new IntraTransaction(new Wallet(), TransactionType.WITHDRAWAL, 10.0));
        transactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 100.0));

        when(walletService.isUserAuthorized(userId, walletId)).thenReturn(true);
        when(transactionService.getTransactionHistory(walletId, SortOrder.DESC, null)).thenReturn(transactions);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/wallets/{walletId}/transactions?sortOrder=DESC", userId, walletId))
                .andExpect(status().isOk())
                .andReturn();

        List<Object> responseBody = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(transactions.size(), responseBody.size());

        verify(transactionService, times(1)).getTransactionHistory(walletId, SortOrder.DESC, null);
    }

    @Test
    void testGetTransactionHistoryWhenTypeDeposit() throws Exception {
        List<Object> transactions = new ArrayList<>();
        transactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 10.0));
        transactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 70.0));

        when(walletService.isUserAuthorized(userId, walletId)).thenReturn(true);
        when(transactionService.getTransactionHistory(walletId, null, TransactionType.DEPOSIT)).thenReturn(transactions);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/wallets/{walletId}/transactions?transactionType=DEPOSIT", userId, walletId))
                .andExpect(status().isOk())
                .andReturn();

        List<Object> responseBody = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(transactions.size(), responseBody.size());

        verify(transactionService, times(1)).getTransactionHistory(walletId, null, TransactionType.DEPOSIT);
    }

    @Test
    void testGetTransactionHistoryWhenTypeWithdrawal() throws Exception {
        List<Object> transactions = new ArrayList<>();
        transactions.add(new IntraTransaction(new Wallet(), TransactionType.WITHDRAWAL, 10.0));
        transactions.add(new IntraTransaction(new Wallet(), TransactionType.WITHDRAWAL, 70.0));

        when(walletService.isUserAuthorized(userId, walletId)).thenReturn(true);
        when(transactionService.getTransactionHistory(walletId, null, TransactionType.WITHDRAWAL)).thenReturn(transactions);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/wallets/{walletId}/transactions?transactionType=WITHDRAWAL", userId, walletId))
                .andExpect(status().isOk())
                .andReturn();

        List<Object> responseBody = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(transactions.size(), responseBody.size());

        verify(transactionService, times(1)).getTransactionHistory(walletId, null, TransactionType.WITHDRAWAL);
    }

    @Test
    void testGetTransactionHistoryWhenTypeTransfer() throws Exception {
        List<Object> transactions = new ArrayList<>();
        transactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 10.0));
        transactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 70.0));

        when(walletService.isUserAuthorized(userId, walletId)).thenReturn(true);
        when(transactionService.getTransactionHistory(walletId, null, TransactionType.TRANSFER)).thenReturn(transactions);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/wallets/{walletId}/transactions?transactionType=TRANSFER", userId, walletId))
                .andExpect(status().isOk())
                .andReturn();

        List<Object> responseBody = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(transactions.size(), responseBody.size());

        verify(transactionService, times(1)).getTransactionHistory(walletId, null, TransactionType.TRANSFER);
    }

    @Test
    void testGetTransactionHistoryWhenSortAscendingAndTypeTransfer() throws Exception {
        List<Object> transactions = new ArrayList<>();
        transactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 10.0));
        transactions.add(new InterTransaction(new Wallet(), new Wallet(), TransactionType.TRANSFER, 70.0));

        when(walletService.isUserAuthorized(userId, walletId)).thenReturn(true);
        when(transactionService.getTransactionHistory(walletId, SortOrder.ASC, TransactionType.TRANSFER)).thenReturn(transactions);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/wallets/{walletId}/transactions?sortOrder=ASC&transactionType=TRANSFER", userId, walletId))
                .andExpect(status().isOk())
                .andReturn();

        List<Object> responseBody = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(transactions.size(), responseBody.size());

        verify(transactionService, times(1)).getTransactionHistory(walletId, SortOrder.ASC, TransactionType.TRANSFER);
    }

    @Test
    void testGetTransactionHistoryWhenSortDescendingAndTypeDeposit() throws Exception {
        List<Object> transactions = new ArrayList<>();
        transactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 100.0));
        transactions.add(new IntraTransaction(new Wallet(), TransactionType.DEPOSIT, 200.0));

        when(walletService.isUserAuthorized(userId, walletId)).thenReturn(true);
        when(transactionService.getTransactionHistory(walletId, SortOrder.DESC, TransactionType.DEPOSIT)).thenReturn(transactions);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/wallets/{walletId}/transactions?sortOrder=DESC&transactionType=DEPOSIT", userId, walletId))
                .andExpect(status().isOk())
                .andReturn();

        List<Object> responseBody = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(transactions.size(), responseBody.size());

        verify(transactionService, times(1)).getTransactionHistory(walletId, SortOrder.DESC, TransactionType.DEPOSIT);
    }
}
