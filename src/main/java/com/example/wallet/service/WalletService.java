package com.example.wallet.service;

import com.example.wallet.Exceptions.UserNotFoundException;
import com.example.wallet.model.Wallet;
import com.example.wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WalletService {
    private final WalletRepository walletRepository;

    @Autowired
    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public BigDecimal deposit(Long userId, BigDecimal amount) {
        Wallet wallet = walletRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        return wallet.deposit(amount);
    }

    public BigDecimal withdraw(Long userId, BigDecimal amount) {
        Wallet wallet = walletRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        return wallet.withdraw(amount);
    }
}
