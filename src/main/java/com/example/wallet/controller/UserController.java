package com.example.wallet.controller;

import com.example.wallet.model.User;
import com.example.wallet.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestParam String username, @RequestParam String password) {
        try {
            User user = userService.registerUser(username, password);
            return ResponseEntity.ok(user);}
        catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred: "+ e.getMessage());
        }
    }
}
