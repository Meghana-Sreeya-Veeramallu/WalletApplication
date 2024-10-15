package com.example.wallet.controller;

import com.example.wallet.Exceptions.*;
import com.example.wallet.model.Transaction;
import com.example.wallet.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/wallets")
public class TransactionController {
    private final TransactionService transactionService;

    @Autowired
    public TransactionController (TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/{walletId}/transactions")
    public ResponseEntity<?> getTransactionHistory(@PathVariable Long userId, @PathVariable Long walletId) {
        try {
            List<Transaction> transactions = transactionService.getTransactionHistory(userId);
            return ResponseEntity.ok(transactions);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }
    }
}
