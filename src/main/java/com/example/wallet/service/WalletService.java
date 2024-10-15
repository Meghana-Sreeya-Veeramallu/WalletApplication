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
    public Double deposit(Long walletId, Double amount) {
        Wallet wallet = walletRepository.findById(walletId).orElseThrow(() -> new UserNotFoundException("User not found"));;
        Double newBalance = wallet.deposit(amount);
        walletRepository.save(wallet);

        Transaction transaction = new Transaction(wallet, TransactionType.DEPOSIT, amount);
        transactionRepository.save(transaction);

        return newBalance;
    }

    @Transactional
    public Double withdraw(Long walletId, Double amount) {
        Wallet wallet = walletRepository.findById(walletId).orElseThrow(() -> new UserNotFoundException("User not found"));;
        Double newBalance = wallet.withdraw(amount);
        walletRepository.save(wallet);

        Transaction transaction = new Transaction(wallet, TransactionType.WITHDRAWAL, amount);
        transactionRepository.save(transaction);

        return newBalance;
    }

    @Transactional
    public Double transfer(Long senderWalletId, Long recipientWalletId, Double amount) {
        Wallet senderWallet = walletRepository.findById(senderWalletId)
                .orElseThrow(() -> new UserNotFoundException("Sender not found"));
        Wallet recipientWallet = walletRepository.findById(recipientWalletId)
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

    public boolean isUserWalletOwner(Long userId, Long walletId) {
        Long walletIdFromUserId = walletRepository.findIdByUserId(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        return walletIdFromUserId.equals(walletId);
    }
}
