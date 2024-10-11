package com.example.wallet.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.wallet.Exceptions.PasswordCannotBeNullOrEmptyException;
import com.example.wallet.model.User;
import com.example.wallet.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
     void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
     void testRegisterUser() {
        User user = new User("testUser", "testPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        String password = "testPassword";
        String username = "testUser";

        User registeredUser = userService.registerUser(username, password);

        assertNotNull(registeredUser);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
     void testRegisterInvalidUser() {
        String password = "";
        String username = "testUser";

        assertThrows(PasswordCannotBeNullOrEmptyException.class , () ->userService.registerUser(username, password));
        verify(userRepository, times(0)).save(any(User.class));
    }
}

