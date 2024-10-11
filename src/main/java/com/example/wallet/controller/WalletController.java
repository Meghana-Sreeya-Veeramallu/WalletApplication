package com.example.wallet.controller;

import com.example.wallet.Exceptions.DepositAmountMustBePositiveException;
import com.example.wallet.Exceptions.InsufficientFundsException;
import com.example.wallet.Exceptions.UserNotFoundException;
import com.example.wallet.Exceptions.WithdrawAmountMustBePositiveException;
import com.example.wallet.dto.TransactionDto;
import com.example.wallet.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class WalletController {
    private final WalletService walletService;

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestBody @Valid TransactionDto request) {
        try {
            Double amount = walletService.deposit(request.getUsername(), request.getAmount());
            return ResponseEntity.ok(amount);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (DepositAmountMustBePositiveException e) {
            return ResponseEntity.badRequest().body("Bad request: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@RequestBody @Valid TransactionDto request) {
        try {
            Double amount = walletService.withdraw(request.getUsername(), request.getAmount());
            return ResponseEntity.ok(amount);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (WithdrawAmountMustBePositiveException | InsufficientFundsException e) {
            return ResponseEntity.badRequest().body("Bad request: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }
    }
}
