package com.example.wallet.controller;

import com.example.wallet.Exceptions.PasswordCannotBeNullOrEmptyException;
import com.example.wallet.Exceptions.UsernameCannotBeNullOrEmptyException;
import com.example.wallet.dto.RegistrationRequestBody;
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
        RegistrationRequestBody request = new RegistrationRequestBody(username, password);
        String requestBody = objectMapper.writeValueAsString(request);
        User mockUser = new User(username, password);

        when(userService.registerUser(username, password)).thenReturn(mockUser);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
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
        RegistrationRequestBody request = new RegistrationRequestBody(username, password);
        String requestBody = objectMapper.writeValueAsString(request);

        when(userService.registerUser(username, password)).thenThrow(new UsernameCannotBeNullOrEmptyException("Username cannot be null or empty"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request: Username cannot be null or empty"));

        verify(userService, times(1)).registerUser(username, password);
    }

    @Test
    void testRegisterWhenPasswordIsNull() throws Exception {
        String username = "testUser";
        String password = "";
        RegistrationRequestBody request = new RegistrationRequestBody(username, password);
        String requestBody = objectMapper.writeValueAsString(request);

        when(userService.registerUser(username, password)).thenThrow(new PasswordCannotBeNullOrEmptyException("Password cannot be null or empty"));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request: Password cannot be null or empty"));

        verify(userService, times(1)).registerUser(username, password);
    }
}
