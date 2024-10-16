package com.example.wallet.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.wallet.Enums.CurrencyType;
import com.example.wallet.Exceptions.*;
import com.example.wallet.model.User;
import com.example.wallet.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UserServiceTest {
    String username;
    String password;
    User user;

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
     void setUp() {
        MockitoAnnotations.openMocks(this);
        username = "testUser";
        password = "testPassword";
        user = new User(username, password, CurrencyType.INR);
    }

    @Test
     void testRegisterUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);
        String password = "testPassword";
        String username = "testUser";

        User registeredUser = userService.registerUser(username, password, CurrencyType.INR);

        assertNotNull(registeredUser);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUserWithUSDCurrency() {
        when(userRepository.save(any(User.class))).thenReturn(user);
        String password = "testPassword";
        String username = "testUser";

        User registeredUser = userService.registerUser(username, password, CurrencyType.USD);

        assertNotNull(registeredUser);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
     void testRegisterInvalidPassword() {
        String password = "";
        String username = "testUser";

        assertThrows(PasswordCannotBeNullOrEmptyException.class , () -> userService.registerUser(username, password, CurrencyType.INR));
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    void testRegisterInvalidUsername() {
        String password = "testPassword";
        String username = "";

        assertThrows(UsernameCannotBeNullOrEmptyException.class , () -> userService.registerUser(username, password, CurrencyType.INR));
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    void testRegisterUserWithNullCurrency() {
        when(userRepository.save(any(User.class))).thenReturn(user);
        String password = "testPassword";
        String username = "testUser";

        assertThrows(CurrencyCannotBeNullException.class, () ->  userService.registerUser(username, password, null));
        verify(userRepository, times(0)).save(any(User.class));
    }
}
