package com.example.wallet.controller;

import com.example.wallet.Exceptions.PasswordCannotBeNullOrEmptyException;
import com.example.wallet.Exceptions.UsernameCannotBeNullOrEmptyException;
import com.example.wallet.model.User;
import com.example.wallet.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterWhenSuccessful() {
        String username = "testUser";
        String password = "testPassword";
        User mockUser = new User(username, password);
        when(userService.registerUser(username, password)).thenReturn(mockUser);

        ResponseEntity<?> response = userController.register(username, password);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockUser, response.getBody());
    }

    @Test
    void testRegisterWhenUserIsNull() {
        String username = "";
        String password = "testPassword";
        when(userService.registerUser(username, password)).thenThrow(new UsernameCannotBeNullOrEmptyException("Username cannot be null or empty"));

        ResponseEntity<?> response = userController.register(username, password);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred: Username cannot be null or empty", response.getBody());
    }

    @Test
    void testRegisterWhenPasswordIsNull() {
        String username = "testUser";
        String password = "";
        when(userService.registerUser(username, password)).thenThrow(new PasswordCannotBeNullOrEmptyException("Password cannot be null or empty"));

        ResponseEntity<?> response = userController.register(username, password);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred: Password cannot be null or empty", response.getBody());
    }
}
