package com.example.wallet.controller;

import com.example.wallet.Exceptions.*;
import com.example.wallet.dto.TransferDto;
import com.example.wallet.dto.WalletDto;
import com.example.wallet.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deposit(@PathVariable Long userId, @PathVariable Long walletId, @RequestBody @Valid WalletDto request) {
        try {
            if (!walletService.isUserAuthorized(userId, walletId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: User is not authorized");
            }
            walletService.deposit(walletId, request.getAmount());
            String successMessage = "Amount deposited successfully: " + request.getAmount();
            return ResponseEntity.ok(successMessage);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (DepositAmountMustBePositiveException e) {
            return ResponseEntity.badRequest().body("Bad request: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/{walletId}/withdrawal")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> withdraw(@PathVariable Long userId, @PathVariable Long walletId, @RequestBody @Valid WalletDto request) {
        try {
            if (!walletService.isUserAuthorized(userId, walletId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: User is not authorized");
            }
            walletService.withdraw(walletId, request.getAmount());
            String successMessage = "Amount withdrawn successfully: " + request.getAmount();
            return ResponseEntity.ok(successMessage);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (WithdrawAmountMustBePositiveException | InsufficientFundsException e) {
            return ResponseEntity.badRequest().body("Bad request: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/{walletId}/transfer")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> transfer(@PathVariable Long userId, @PathVariable Long walletId,
                                      @RequestBody @Valid TransferDto request) {
        try {
            if (!walletService.isUserAuthorized(userId, walletId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: User is not authorized");
            }
            walletService.transfer(walletId, request.getRecipientWalletId(), request.getAmount());
            String successMessage = "Amount transferred successfully: " + request.getAmount();
            return ResponseEntity.ok(successMessage);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (TransferAmountMustBePositiveException | InsufficientFundsException e) {
            return ResponseEntity.badRequest().body("Bad request: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }
    }
}
