package com.example.wallet.service;

import com.example.wallet.Exceptions.UserNotFoundException;
import com.example.wallet.model.Wallet;
import com.example.wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WalletService {
    private final WalletRepository walletRepository;

    @Autowired
    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Transactional
    public Double deposit(Long userId, Double amount) {
        Wallet wallet = walletRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException("User not found"));;
        return wallet.deposit(amount);
    }

    @Transactional
    public Double withdraw(Long userId, Double amount) {
        Wallet wallet = walletRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException("User not found"));;
        return wallet.withdraw(amount);
    }
}
