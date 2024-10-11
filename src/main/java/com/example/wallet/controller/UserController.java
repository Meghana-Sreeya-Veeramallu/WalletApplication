package com.example.wallet.controller;

import com.example.wallet.Exceptions.PasswordCannotBeNullOrEmptyException;
import com.example.wallet.Exceptions.UsernameCannotBeNullOrEmptyException;
import com.example.wallet.dto.RegistrationRequestBody;
import com.example.wallet.model.User;
import com.example.wallet.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegistrationRequestBody request) {
        try {
            User user = userService.registerUser(request.getUsername(), request.getPassword());
            return ResponseEntity.ok(user);
        } catch (UsernameCannotBeNullOrEmptyException | PasswordCannotBeNullOrEmptyException e) {
            return ResponseEntity.badRequest().body("Bad request: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }
    }
}
