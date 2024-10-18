package com.example.wallet.controller;

import com.example.wallet.Exceptions.AmountCannotBeNullException;
import com.example.wallet.Exceptions.InvalidTransactionTypeException;
import com.example.wallet.dto.TransactionDto;
import com.example.wallet.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/wallets/{walletId}")
public class TransactionController {
    private final TransactionService transactionService;

    @Autowired
    public TransactionController (TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transactions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createTransaction(
            @PathVariable Long userId,
            @PathVariable Long walletId,
            @RequestBody TransactionDto request) {

        validateTransactionRequest(request);
        transactionService.createTransaction(userId, walletId, request.getRecipientWalletId(), request.getAmount(), request.getTransactionType().toLowerCase());
        String successMessage = "Transaction successful: " + request.getTransactionType().toLowerCase() + " of " + request.getAmount();
        return ResponseEntity.ok(successMessage);
    }

    @GetMapping("/transactions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getTransactions(@PathVariable Long userId, @PathVariable Long walletId,
                                             @RequestParam(required = false) String sortBy,
                                             @RequestParam(required = false) String sortOrder,
                                             @RequestParam(required = false) String transactionType) {
        List<Object> transactions = transactionService.getTransactions(userId, walletId, sortBy, sortOrder, transactionType);
        return ResponseEntity.ok(transactions);
    }

    private void validateTransactionRequest(TransactionDto request) {
        if (request.getTransactionType() == null) {
            throw new InvalidTransactionTypeException("Transaction type is required and cannot be null");
        }
        if (request.getAmount() == null) {
            throw new AmountCannotBeNullException("Amount is required and cannot be null");
        }
    }
}
