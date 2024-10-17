package com.example.wallet.controller;

import com.example.wallet.Enums.TransactionType;
import com.example.wallet.Exceptions.*;
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
