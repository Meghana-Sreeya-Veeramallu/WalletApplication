package com.example.wallet.controller;

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
    public ResponseEntity<?> handleTransaction(
            @PathVariable Long userId,
            @PathVariable Long walletId,
            @RequestBody TransactionDto request) {

        if (request.getTransactionType() == null){
            return ResponseEntity.badRequest().body("Transaction type is required and cannot be null");
        }
        if (request.getAmount() == null){
            return ResponseEntity.badRequest().body("Amount is required and cannot be null");
        }

        switch (request.getTransactionType().toLowerCase()) {
            case "deposit":
                transactionService.deposit(userId, walletId, request.getAmount());
                break;
            case "withdrawal":
                transactionService.withdraw(userId, walletId, request.getAmount());
                break;
            case "transfer":
                if (request.getRecipientWalletId() == null) {
                    return ResponseEntity.badRequest().body("Recipient wallet ID is required for transfers");
                }
                transactionService.transfer(userId, walletId, request.getRecipientWalletId(), request.getAmount());
                break;
            default:
                return ResponseEntity.badRequest().body("Invalid transaction type: " + request.getTransactionType().toLowerCase());
        }

        String successMessage = "Transaction successful: " + request.getTransactionType().toLowerCase() + " of " + request.getAmount();
        return ResponseEntity.ok(successMessage);
    }

    @GetMapping("/transactions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getTransactionHistory(@PathVariable Long userId, @PathVariable Long walletId,
                                                   @RequestParam(required = false) String sortBy,
                                                   @RequestParam(required = false) String sortOrder,
                                                   @RequestParam(required = false) String transactionType) {
        List<Object> transactions = transactionService.getTransactionHistory(userId, walletId, sortBy, sortOrder, transactionType);
        return ResponseEntity.ok(transactions);
    }
}
