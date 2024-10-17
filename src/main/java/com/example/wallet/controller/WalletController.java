package com.example.wallet.controller;

import com.example.wallet.dto.TransferDto;
import com.example.wallet.dto.WalletDto;
import com.example.wallet.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
        walletService.deposit(userId, walletId, request.getAmount());
        String successMessage = "Amount deposited successfully: " + request.getAmount();
        return ResponseEntity.ok(successMessage);
    }

    @PostMapping("/{walletId}/withdrawal")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> withdraw(@PathVariable Long userId, @PathVariable Long walletId, @RequestBody @Valid WalletDto request) {
        walletService.withdraw(userId, walletId, request.getAmount());
        String successMessage = "Amount withdrawn successfully: " + request.getAmount();
        return ResponseEntity.ok(successMessage);
    }

    @PostMapping("/{walletId}/transfer")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> transfer(@PathVariable Long userId, @PathVariable Long walletId,
                                      @RequestBody @Valid TransferDto request) {
        walletService.transfer(userId, walletId, request.getRecipientWalletId(), request.getAmount());
        String successMessage = "Amount transferred successfully: " + request.getAmount();
        return ResponseEntity.ok(successMessage);
    }
}
