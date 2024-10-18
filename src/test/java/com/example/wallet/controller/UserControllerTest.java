package com.example.wallet.controller;

import com.example.wallet.Enums.CurrencyType;
import com.example.wallet.Exceptions.*;
import com.example.wallet.dto.RegistrationDto;
import com.example.wallet.model.User;
import com.example.wallet.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        mockMvc = MockMvcBuilders.standaloneSetup(userController).setControllerAdvice(new GlobalExceptionHandler()).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testRegisterWhenSuccessful() throws Exception {
        String username = "testUser";
        String password = "testPassword";
        RegistrationDto requestBody = new RegistrationDto(username, password, CurrencyType.INR);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);
        User mockUser = new User(username, password, CurrencyType.INR);

        when(userService.registerUser(username, password, CurrencyType.INR)).thenReturn(mockUser);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isOk()).andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        String expectedMessage = "Successfully registered user: " + username;

        assertEquals(expectedMessage, response);
        verify(userService, times(1)).registerUser(username, password, CurrencyType.INR);
    }

    @Test
    void testRegisterWhenSuccessfulWithUSDCurrency() throws Exception {
        String username = "testUser";
        String password = "testPassword";
        RegistrationDto requestBody = new RegistrationDto(username, password, CurrencyType.USD);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);
        User mockUser = new User(username, password, CurrencyType.USD);

        when(userService.registerUser(username, password, CurrencyType.USD)).thenReturn(mockUser);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isOk()).andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        String expectedMessage = "Successfully registered user: " + username;

        assertEquals(expectedMessage, response);
        verify(userService, times(1)).registerUser(username, password, CurrencyType.USD);
    }

    @Test
    void testRegisterWhenUserIsNull() throws Exception {
        String username = "";
        String password = "testPassword";
        RegistrationDto requestBody = new RegistrationDto(username, password, CurrencyType.INR);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(userService.registerUser(username, password, CurrencyType.INR)).thenThrow(new UsernameCannotBeNullOrEmptyException("Username cannot be null or empty"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request: Username cannot be null or empty"));

        verify(userService, times(1)).registerUser(username, password, CurrencyType.INR);
    }

    @Test
    void testRegisterWhenPasswordIsNull() throws Exception {
        String username = "testUser";
        String password = "";
        RegistrationDto requestBody = new RegistrationDto(username, password, CurrencyType.INR);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(userService.registerUser(username, password, CurrencyType.INR)).thenThrow(new PasswordCannotBeNullOrEmptyException("Password cannot be null or empty"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request: Password cannot be null or empty"));

        verify(userService, times(1)).registerUser(username, password, CurrencyType.INR);
    }

    @Test
    void testRegisterWhenCurrencyIsNull() throws Exception {
        String username = "testUser";
        String password = "testPassword";
        RegistrationDto requestBody = new RegistrationDto(username, password, null);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        when(userService.registerUser(username, password, null)).thenThrow(new CurrencyCannotBeNullException("Currency cannot be null"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request: Currency cannot be null"));

        verify(userService, times(1)).registerUser(username, password, null);
    }

    @Test
    void testRegisterWhenUsernameAlreadyExists() throws Exception {
        String username = "testUser";
        String password = "testPassword";
        RegistrationDto requestBody = new RegistrationDto(username, password, CurrencyType.INR);
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);
        User mockUser = new User(username, password, CurrencyType.INR);

        when(userService.registerUser(username, password, CurrencyType.INR)).thenThrow(new DataIntegrityViolationException("Username already exists"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad Request: Username already exists"));

        verify(userService, times(1)).registerUser(username, password, CurrencyType.INR);
    }
}
