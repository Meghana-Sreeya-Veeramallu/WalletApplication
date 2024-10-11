package com.example.wallet.controller;

import com.example.wallet.service.WalletService;
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
    public ResponseEntity<?> deposit(@RequestParam String username, @RequestParam Double amount) {
        try{
            return ResponseEntity.ok(walletService.deposit(username, amount));
        } catch (Exception e){
            return ResponseEntity.internalServerError().body("An error occurred: "+ e.getMessage());
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@RequestParam String username, @RequestParam Double amount) {
        try{
            return ResponseEntity.ok(walletService.withdraw(username, amount));
        } catch (Exception e){
            return ResponseEntity.internalServerError().body("An error occurred: "+ e.getMessage());
        }
    }
}
