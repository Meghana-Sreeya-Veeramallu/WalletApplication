package com.example.wallet.controller;

import com.example.wallet.dto.TransactionRequestBody;
import com.example.wallet.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {
    private final WalletService walletService;

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestBody @Valid TransactionRequestBody request) {
        try{
            return ResponseEntity.ok(walletService.deposit(request.getUsername(), request.getAmount()));
        } catch (Exception e){
            return ResponseEntity.internalServerError().body("An error occurred: "+ e.getMessage());
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@RequestBody @Valid TransactionRequestBody request) {
        try{
            return ResponseEntity.ok(walletService.withdraw(request.getUsername(), request.getAmount()));
        } catch (Exception e){
            return ResponseEntity.internalServerError().body("An error occurred: "+ e.getMessage());
        }
    }
}
