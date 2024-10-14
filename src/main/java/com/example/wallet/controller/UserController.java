package com.example.wallet.controller;

import com.example.wallet.Exceptions.*;
import com.example.wallet.dto.RegistrationDto;
import com.example.wallet.dto.TransactionDto;
import com.example.wallet.model.User;
import com.example.wallet.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> register(@RequestBody @Valid RegistrationDto request) {
        try {
            User user = userService.registerUser(request.getUsername(), request.getPassword());
            return ResponseEntity.ok(user);
        } catch (UsernameCannotBeNullOrEmptyException | PasswordCannotBeNullOrEmptyException e) {
            return ResponseEntity.badRequest().body("Bad request: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestBody @Valid TransactionDto request) {
        try {
            Double amount = userService.deposit(request.getUsername(), request.getPassword(), request.getAmount());
            return ResponseEntity.ok(amount);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (DepositAmountMustBePositiveException | CredentialsDoNotMatchException e) {
            return ResponseEntity.badRequest().body("Bad request: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@RequestBody @Valid TransactionDto request) {
        try {
            Double amount = userService.withdraw(request.getUsername(), request.getPassword(), request.getAmount());
            return ResponseEntity.ok(amount);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (WithdrawAmountMustBePositiveException | InsufficientFundsException | CredentialsDoNotMatchException e) {
            return ResponseEntity.badRequest().body("Bad request: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }
    }
}
