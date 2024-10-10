package com.example.wallet.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.wallet.Exceptions.PasswordCannotBeNullOrEmptyException;
import com.example.wallet.Exceptions.UserNotFoundException;
import com.example.wallet.model.User;
import com.example.wallet.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterUser() {
        User user = new User("testUser", "testPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        String password = "testPassword";
        String username = "testUser";

        User registeredUser = userService.registerUser(username, password);

        assertNotNull(registeredUser);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testRegisterInvalidUser() {
        String password = "";
        String username = "testUser";

        assertThrows(PasswordCannotBeNullOrEmptyException.class , () ->userService.registerUser(username, password));
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    public void testFindById() {
        User user = new User("testUser", "testPassword");
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User foundUser = userService.findById(userId);

        assertNotNull(foundUser);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testFindByIdWhenUserIsNotFound() {
        Long invalidUserId = 2L;

        assertThrows(UserNotFoundException.class, () -> {
            userService.findById(invalidUserId);
        });
        verify(userRepository, times(1)).findById(invalidUserId);
    }
}

