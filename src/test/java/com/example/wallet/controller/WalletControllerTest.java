package com.example.wallet.controller;

import com.example.wallet.Exceptions.DepositAmountMustBePositiveException;
import com.example.wallet.Exceptions.InsufficientFundsException;
import com.example.wallet.Exceptions.UserNotFoundException;
import com.example.wallet.Exceptions.WithdrawAmountMustBePositiveException;
import com.example.wallet.dto.TransactionDto;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WalletControllerTest {

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
    }

    @Test
    void testDepositWhenSuccessful() throws Exception {
        String username = "testUser";
        Double amount = 100.0;
        TransactionDto requestBody = new TransactionDto(username, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(walletService.deposit(username, amount)).thenReturn(amount);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/users/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isOk())
                .andReturn();

        Double responseBody = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Double.class);
        assertEquals(amount, responseBody);

        verify(walletService, times(1)).deposit(username, amount);
    }

    @Test
    void testDepositWhenUserNotFoundException() throws Exception {
        String username = "invalidUser";
        Double amount = 100.0;
        TransactionDto requestBody = new TransactionDto(username, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(walletService.deposit(username, amount)).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));

        verify(walletService, times(1)).deposit(username, amount);
    }

    @Test
    void testDepositWhenDepositAmountIsNegative() throws Exception {
        String username = "testUser";
        Double amount = -100.0;
        TransactionDto requestBody = new TransactionDto(username, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(walletService.deposit(username, amount)).thenThrow(new DepositAmountMustBePositiveException("Deposit amount must be positive"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request: Deposit amount must be positive"));

        verify(walletService, times(1)).deposit(username, amount);
    }

    @Test
    void testWithdrawWhenSuccessful() throws Exception {
        String username = "testUser";
        Double amount = 100.0;
        TransactionDto requestBody = new TransactionDto(username, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(walletService.withdraw(username, amount)).thenReturn(amount);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/users/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isOk())
                .andReturn();;

        Double responseBody = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Double.class);
        assertEquals(amount, responseBody);

        verify(walletService, times(1)).withdraw(username, amount);
    }

    @Test
    void testWithdrawWhenUserNotFoundException() throws Exception {
        String username = "invalidUser";
        Double amount = 100.0;
        TransactionDto requestBody = new TransactionDto(username, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(walletService.withdraw(username, amount)).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));

        verify(walletService, times(1)).withdraw(username, amount);
    }

    @Test
    void testWithdrawWhenWithdrawAmountIsNegative() throws Exception {
        String username = "testUser";
        Double amount = -100.0;
        TransactionDto requestBody = new TransactionDto(username, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(walletService.withdraw(username, amount)).thenThrow(new WithdrawAmountMustBePositiveException("Withdraw amount must be positive"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request: Withdraw amount must be positive"));

        verify(walletService, times(1)).withdraw(username, amount);
    }

    @Test
    void testWithdrawWhenInsufficientFundsException() throws Exception {
        String username = "testUser";
        Double amount = 50.0;
        TransactionDto requestBody = new TransactionDto(username, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(walletService.withdraw(username, amount)).thenThrow(new InsufficientFundsException("Insufficient funds"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request: Insufficient funds"));

        verify(walletService, times(1)).withdraw(username, amount);
    }
}
