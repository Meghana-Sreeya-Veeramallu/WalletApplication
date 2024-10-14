package com.example.wallet.controller;

import com.example.wallet.Exceptions.*;
import com.example.wallet.dto.RegistrationDto;
import com.example.wallet.dto.TransactionDto;
import com.example.wallet.model.User;
import com.example.wallet.service.UserService;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testRegisterWhenSuccessful() throws Exception {
        String username = "testUser";
        String password = "testPassword";
        RegistrationDto requestBody = new RegistrationDto(username, password);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);
        User mockUser = new User(username, password);

        when(userService.registerUser(username, password)).thenReturn(mockUser);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isOk()).andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        User responseUser = objectMapper.readValue(response, User.class);

        assertNotNull(responseUser);
        verify(userService, times(1)).registerUser(username, password);
    }

    @Test
    void testRegisterWhenUserIsNull() throws Exception {
        String username = "";
        String password = "testPassword";
        RegistrationDto requestBody = new RegistrationDto(username, password);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(userService.registerUser(username, password)).thenThrow(new UsernameCannotBeNullOrEmptyException("Username cannot be null or empty"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request: Username cannot be null or empty"));

        verify(userService, times(1)).registerUser(username, password);
    }

    @Test
    void testRegisterWhenPasswordIsNull() throws Exception {
        String username = "testUser";
        String password = "";
        RegistrationDto requestBody = new RegistrationDto(username, password);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(userService.registerUser(username, password)).thenThrow(new PasswordCannotBeNullOrEmptyException("Password cannot be null or empty"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request: Password cannot be null or empty"));

        verify(userService, times(1)).registerUser(username, password);
    }

    @Test
    void testDepositWhenSuccessful() throws Exception {
        String username = "testUser";
        String password = "testPassword";
        Double amount = 100.0;
        TransactionDto requestBody = new TransactionDto(username, password, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(userService.deposit(username, password, amount)).thenReturn(amount);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/users/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isOk())
                .andReturn();

        Double responseBody = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Double.class);
        assertEquals(amount, responseBody);

        verify(userService, times(1)).deposit(username, password, amount);
    }

    @Test
    void testDepositWhenUserNotFoundException() throws Exception {
        String username = "invalidUser";
        String password = "testPassword";
        Double amount = 100.0;
        TransactionDto requestBody = new TransactionDto(username, password, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(userService.deposit(username, password, amount)).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));

        verify(userService, times(1)).deposit(username, password,amount);
    }

    @Test
    void testDepositWhenCredentialsDoNotMatch() throws Exception {
        String username = "testUser";
        String password = "invalidPassword";
        Double amount = 100.0;
        TransactionDto requestBody = new TransactionDto(username, password, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(userService.deposit(username, password, amount)).thenThrow(new CredentialsDoNotMatchException("Credentials do not match"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request: Credentials do not match"));

        verify(userService, times(1)).deposit(username, password,amount);
    }

    @Test
    void testDepositWhenDepositAmountIsNegative() throws Exception {
        String username = "testUser";
        String password = "testPassword";
        Double amount = -100.0;
        TransactionDto requestBody = new TransactionDto(username, password, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(userService.deposit(username, password, amount)).thenThrow(new DepositAmountMustBePositiveException("Deposit amount must be positive"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request: Deposit amount must be positive"));

        verify(userService, times(1)).deposit(username, password, amount);
    }

    @Test
    void testWithdrawWhenSuccessful() throws Exception {
        String username = "testUser";
        String password = "testPassword";
        Double amount = 100.0;
        TransactionDto requestBody = new TransactionDto(username, password, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(userService.withdraw(username, password, amount)).thenReturn(amount);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/users/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isOk())
                .andReturn();;

        Double responseBody = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Double.class);
        assertEquals(amount, responseBody);

        verify(userService, times(1)).withdraw(username, password, amount);
    }

    @Test
    void testWithdrawWhenUserNotFoundException() throws Exception {
        String username = "invalidUser";
        String password = "testPassword";
        Double amount = 100.0;
        TransactionDto requestBody = new TransactionDto(username, password, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(userService.withdraw(username, password, amount)).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));

        verify(userService, times(1)).withdraw(username, password, amount);
    }

    @Test
    void testWithdrawWhenCredentialsDoNotMatch() throws Exception {
        String username = "testUser";
        String password = "invalidPassword";
        Double amount = 100.0;
        TransactionDto requestBody = new TransactionDto(username, password, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(userService.withdraw(username, password, amount)).thenThrow(new CredentialsDoNotMatchException("Credentials do not match"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request: Credentials do not match"));

        verify(userService, times(1)).withdraw(username, password,amount);
    }

    @Test
    void testWithdrawWhenWithdrawAmountIsNegative() throws Exception {
        String username = "testUser";
        String password = "testPassword";
        Double amount = -100.0;
        TransactionDto requestBody = new TransactionDto(username, password, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(userService.withdraw(username, password, amount)).thenThrow(new WithdrawAmountMustBePositiveException("Withdraw amount must be positive"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request: Withdraw amount must be positive"));

        verify(userService, times(1)).withdraw(username, password, amount);
    }

    @Test
    void testWithdrawWhenInsufficientFundsException() throws Exception {
        String username = "testUser";
        String password = "testPassword";
        Double amount = 50.0;
        TransactionDto requestBody = new TransactionDto(username, password, amount);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(userService.withdraw(username, password, amount)).thenThrow(new InsufficientFundsException("Insufficient funds"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request: Insufficient funds"));

        verify(userService, times(1)).withdraw(username, password, amount);
    }
}
