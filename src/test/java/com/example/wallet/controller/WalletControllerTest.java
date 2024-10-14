package com.example.wallet.controller;

import com.example.wallet.Exceptions.*;
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
        Long userId = 1L;
        Double amount = 100.0;
        WalletDto requestBody = new WalletDto(userId, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(walletService.deposit(userId, amount)).thenReturn(amount);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/wallets/1/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isOk())
                .andReturn();

        Double responseBody = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Double.class);
        assertEquals(amount, responseBody);

        verify(walletService, times(1)).deposit(userId, amount);
    }

    @Test
    void testDepositWhenUserNotFoundException() throws Exception {
        Long userId = 1L;
        Double amount = 100.0;
        WalletDto requestBody = new WalletDto(userId, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(walletService.deposit(userId, amount)).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.post("/wallets/1/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));

        verify(walletService, times(1)).deposit(userId,amount);
    }

    @Test
    void testDepositWhenDepositAmountIsNegative() throws Exception {
        Long userId = 1L;
        Double amount = -100.0;
        WalletDto requestBody = new WalletDto(userId, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(walletService.deposit(userId, amount)).thenThrow(new DepositAmountMustBePositiveException("Deposit amount must be positive"));

        mockMvc.perform(MockMvcRequestBuilders.post("/wallets/1/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request: Deposit amount must be positive"));

        verify(walletService, times(1)).deposit(userId, amount);
    }

    @Test
    void testWithdrawWhenSuccessful() throws Exception {
        Long userId = 1L;
        Double amount = 100.0;
        WalletDto requestBody = new WalletDto(userId, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(walletService.withdraw(userId, amount)).thenReturn(amount);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/wallets/1/withdrawal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isOk())
                .andReturn();;

        Double responseBody = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Double.class);
        assertEquals(amount, responseBody);

        verify(walletService, times(1)).withdraw(userId, amount);
    }

    @Test
    void testWithdrawWhenUserNotFoundException() throws Exception {
        Long userId = 1L;
        Double amount = 100.0;
        WalletDto requestBody = new WalletDto(userId, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(walletService.withdraw(userId, amount)).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.post("/wallets/1/withdrawal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));

        verify(walletService, times(1)).withdraw(userId, amount);
    }

    @Test
    void testWithdrawWhenWithdrawAmountIsNegative() throws Exception {
        Long userId = 1L;
        Double amount = -100.0;
        WalletDto requestBody = new WalletDto(userId, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(walletService.withdraw(userId, amount)).thenThrow(new WithdrawAmountMustBePositiveException("Withdraw amount must be positive"));

        mockMvc.perform(MockMvcRequestBuilders.post("/wallets/1/withdrawal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request: Withdraw amount must be positive"));

        verify(walletService, times(1)).withdraw(userId, amount);
    }

    @Test
    void testWithdrawWhenInsufficientFundsException() throws Exception {
        Long userId = 1L;
        Double amount = 50.0;
        WalletDto requestBody = new WalletDto(userId, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(walletService.withdraw(userId, amount)).thenThrow(new InsufficientFundsException("Insufficient funds"));

        mockMvc.perform(MockMvcRequestBuilders.post("/wallets/1/withdrawal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request: Insufficient funds"));

        verify(walletService, times(1)).withdraw(userId, amount);
    }

    @Test
    void testTransferWhenSuccessful() throws Exception {
        Long senderId = 1L;
        Long recipientId = 2L;
        Double amount = 30.0;
        WalletDto requestBody = new WalletDto(senderId, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(walletService.transfer(senderId, recipientId, amount)).thenReturn(70.0);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/wallets/{senderId}/transfer/{recipientId}", senderId, recipientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isOk())
                .andReturn();

        Double responseBody = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Double.class);
        assertEquals(70.0, responseBody);

        verify(walletService, times(1)).transfer(senderId, recipientId, amount);
    }

    @Test
    void testTransferWhenSenderNotFoundException() throws Exception {
        Long senderId = 1L;
        Long recipientId = 2L;
        Double amount = 30.0;
        WalletDto requestBody = new WalletDto(senderId, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(walletService.transfer(senderId, recipientId, amount)).thenThrow(new UserNotFoundException("Sender not found"));

        mockMvc.perform(MockMvcRequestBuilders.post("/wallets/{senderId}/transfer/{recipientId}", senderId, recipientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));

        verify(walletService, times(1)).transfer(senderId, recipientId, amount);
    }

    @Test
    void testTransferWhenRecipientNotFoundException() throws Exception {
        Long senderId = 1L;
        Long recipientId = 2L;
        Double amount = 30.0;
        WalletDto requestBody = new WalletDto(senderId, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(walletService.transfer(senderId, recipientId, amount)).thenThrow(new UserNotFoundException("Recipient not found"));

        mockMvc.perform(MockMvcRequestBuilders.post("/wallets/{senderId}/transfer/{recipientId}", senderId, recipientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));

        verify(walletService, times(1)).transfer(senderId, recipientId, amount);
    }

    @Test
    void testTransferWhenInsufficientFundsException() throws Exception {
        Long senderId = 1L;
        Long recipientId = 2L;
        Double amount = 50.0;
        WalletDto requestBody = new WalletDto(senderId, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(walletService.transfer(senderId, recipientId, amount)).thenThrow(new InsufficientFundsException("Insufficient funds"));

        mockMvc.perform(MockMvcRequestBuilders.post("/wallets/{senderId}/transfer/{recipientId}", senderId, recipientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request: Insufficient funds"));

        verify(walletService, times(1)).transfer(senderId, recipientId, amount);
    }

    @Test
    void testTransferWhenTransferAmountIsNegative() throws Exception {
        Long senderId = 1L;
        Long recipientId = 2L;
        Double amount = -30.0;
        WalletDto requestBody = new WalletDto(senderId, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(walletService.transfer(senderId, recipientId, amount)).thenThrow(new TransferAmountMustBePositiveException("Transfer amount must be positive"));

        mockMvc.perform(MockMvcRequestBuilders.post("/wallets/{senderId}/transfer/{recipientId}", senderId, recipientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request: Transfer amount must be positive"));

        verify(walletService, times(1)).transfer(senderId, recipientId, amount);
    }
}
