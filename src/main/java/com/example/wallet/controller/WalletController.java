package com.example.wallet.controller;

import com.example.wallet.Exceptions.*;
import com.example.wallet.dto.TransferDto;
import com.example.wallet.dto.WalletDto;
import com.example.wallet.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/{userId}/wallets")
public class WalletController {
    private final WalletService walletService;

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/{walletId}/deposit")
    public ResponseEntity<?> deposit(@PathVariable Long userId, @PathVariable Long walletId, @RequestBody @Valid WalletDto request) {
        try {
            Double amount = walletService.deposit(userId, request.getAmount());
            return ResponseEntity.ok(amount);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (DepositAmountMustBePositiveException e) {
            return ResponseEntity.badRequest().body("Bad request: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/{walletId}/withdrawal")
    public ResponseEntity<?> withdraw(@PathVariable Long userId, @PathVariable Long walletId, @RequestBody @Valid WalletDto request) {
        try {
            Double amount = walletService.withdraw(userId, request.getAmount());
            return ResponseEntity.ok(amount);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (WithdrawAmountMustBePositiveException | InsufficientFundsException e) {
            return ResponseEntity.badRequest().body("Bad request: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/{walletId}/transfer")
    public ResponseEntity<?> transfer(@PathVariable Long userId, @PathVariable Long walletId,
                                      @RequestBody @Valid TransferDto request) {
        try {
            Double newBalance = walletService.transfer(userId, request.getRecipientId(), request.getAmount());
            return ResponseEntity.ok(newBalance);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (TransferAmountMustBePositiveException | InsufficientFundsException e) {
            return ResponseEntity.badRequest().body("Bad request: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }
    }
}
