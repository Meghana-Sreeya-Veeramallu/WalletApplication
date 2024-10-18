package com.example.wallet.controller;

import com.example.wallet.dto.RegistrationDto;
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

    @PostMapping("")
    public ResponseEntity<?> register(@RequestBody @Valid RegistrationDto request) {
        User user = userService.registerUser(request.getUsername(), request.getPassword(), request.getCurrency());
        String successMessage = "Successfully registered user: " + user.getUsername();
        return ResponseEntity.ok(successMessage);
    }
}
