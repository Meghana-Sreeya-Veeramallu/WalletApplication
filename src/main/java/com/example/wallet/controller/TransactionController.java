package com.example.wallet.controller;

import com.example.wallet.Enums.SortOrder;
import com.example.wallet.Enums.TransactionType;
import com.example.wallet.Exceptions.*;
import com.example.wallet.service.TransactionService;
import com.example.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/wallets")
public class TransactionController {
    private final TransactionService transactionService;
    private final WalletService walletService;

    @Autowired
    public TransactionController (TransactionService transactionService, WalletService walletService) {
        this.transactionService = transactionService;
        this.walletService = walletService;
    }

    @GetMapping("/{walletId}/transactions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getTransactionHistory(@PathVariable Long userId, @PathVariable Long walletId,
                                                   @RequestParam(required = false) SortOrder sortOrder,
                                                   @RequestParam(required = false) TransactionType transactionType) {
        try {
            if (!walletService.isUserAuthorized(userId, walletId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: User is not authorized");
            }
            List<Object> transactions = transactionService.getTransactionHistory(walletId, sortOrder, transactionType);
            return ResponseEntity.ok(transactions);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }
    }
}
