package com.example.wallet.controller;

import com.example.wallet.Exceptions.*;
import com.example.wallet.service.TransactionService;
import com.example.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> getTransactionHistory(@PathVariable Long userId, @PathVariable Long walletId) {
        try {
            if (!walletService.isUserWalletOwner(userId, walletId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Wallet does not belong to user");
            }
            List<Object> intraTransactions = transactionService.getTransactionHistory(walletId);
            return ResponseEntity.ok(intraTransactions);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }
    }
}
