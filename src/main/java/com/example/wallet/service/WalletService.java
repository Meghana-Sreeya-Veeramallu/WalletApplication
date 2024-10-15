package com.example.wallet.service;

import com.example.wallet.Enums.TransactionType;
import com.example.wallet.Exceptions.UserNotFoundException;
import com.example.wallet.model.Transaction;
import com.example.wallet.model.Wallet;
import com.example.wallet.repository.TransactionRepository;
import com.example.wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WalletService {
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public WalletService(WalletRepository walletRepository, TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Double deposit(Long userId, Double amount) {
        Wallet wallet = walletRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException("User not found"));;
        Double newBalance = wallet.deposit(amount);
        walletRepository.save(wallet);

        Transaction transaction = new Transaction(wallet, TransactionType.DEPOSIT, amount);
        transactionRepository.save(transaction);

        return newBalance;
    }

    @Transactional
    public Double withdraw(Long userId, Double amount) {
        Wallet wallet = walletRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException("User not found"));;
        Double newBalance = wallet.withdraw(amount);
        walletRepository.save(wallet);

        Transaction transaction = new Transaction(wallet, TransactionType.WITHDRAWAL, amount);
        transactionRepository.save(transaction);

        return newBalance;
    }

    @Transactional
    public Double transfer(Long senderId, Long recipientId, Double amount) {
        Wallet senderWallet = walletRepository.findByUserId(senderId)
                .orElseThrow(() -> new UserNotFoundException("Sender not found"));
        Wallet recipientWallet = walletRepository.findByUserId(recipientId)
                .orElseThrow(() -> new UserNotFoundException("Recipient not found"));

        Double senderNewBalance = senderWallet.transfer(recipientWallet, amount);
        walletRepository.save(senderWallet);
        walletRepository.save(recipientWallet);

        Transaction senderTransaction = new Transaction(senderWallet, TransactionType.TRANSFER_SEND, amount);
        Transaction recipientTransaction = new Transaction(recipientWallet, TransactionType.TRANSFER_RECEIVE, amount);
        transactionRepository.save(senderTransaction);
        transactionRepository.save(recipientTransaction);

        return senderNewBalance;
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactionHistory(Long userId) {
        Long walletId = walletRepository.findIdByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return transactionRepository.findByWalletId(walletId);
    }
}
