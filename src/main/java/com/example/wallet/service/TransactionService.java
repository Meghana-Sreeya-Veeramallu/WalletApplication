package com.example.wallet.service;

import com.example.wallet.Exceptions.UserNotFoundException;
import com.example.wallet.model.Transaction;
import com.example.wallet.repository.TransactionRepository;
import com.example.wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionService {
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(WalletRepository walletRepository, TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactionHistory(Long userId) {
        Long walletId = walletRepository.findIdByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return transactionRepository.findByWalletId(walletId);
    }
}
