package com.example.wallet.controller;

import com.example.wallet.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getTransactionHistory(@PathVariable Long userId, @PathVariable Long walletId,
                                                   @RequestParam(required = false) String sortBy,
                                                   @RequestParam(required = false) String sortOrder,
                                                   @RequestParam(required = false) String transactionType) {
        List<Object> transactions = transactionService.getTransactionHistory(userId, walletId, sortBy, sortOrder, transactionType);
        return ResponseEntity.ok(transactions);
    }
}
