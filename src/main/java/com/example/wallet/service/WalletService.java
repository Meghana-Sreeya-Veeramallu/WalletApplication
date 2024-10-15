package com.example.wallet.service;

import com.example.wallet.Enums.TransactionType;
import com.example.wallet.Exceptions.UserNotFoundException;
import com.example.wallet.model.InterTransaction;
import com.example.wallet.model.IntraTransaction;
import com.example.wallet.model.Wallet;
import com.example.wallet.repository.InterTransactionRepository;
import com.example.wallet.repository.IntraTransactionRepository;
import com.example.wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WalletService {
    private final WalletRepository walletRepository;
    private final IntraTransactionRepository intraTransactionRepository;
    private final InterTransactionRepository interTransactionRepository;

    @Autowired
    public WalletService(WalletRepository walletRepository, IntraTransactionRepository intraTransactionRepository, InterTransactionRepository interTransactionRepository) {
        this.walletRepository = walletRepository;
        this.intraTransactionRepository = intraTransactionRepository;
        this.interTransactionRepository = interTransactionRepository;
    }

    @Transactional
    public Double deposit(Long walletId, Double amount) {
        Wallet wallet = walletRepository.findById(walletId).orElseThrow(() -> new UserNotFoundException("User not found"));;
        Double newBalance = wallet.deposit(amount);
        walletRepository.save(wallet);

        IntraTransaction intraTransaction = new IntraTransaction(wallet, TransactionType.DEPOSIT, amount);
        intraTransactionRepository.save(intraTransaction);

        return newBalance;
    }

    @Transactional
    public Double withdraw(Long walletId, Double amount) {
        Wallet wallet = walletRepository.findById(walletId).orElseThrow(() -> new UserNotFoundException("User not found"));;
        Double newBalance = wallet.withdraw(amount);
        walletRepository.save(wallet);

        IntraTransaction intraTransaction = new IntraTransaction(wallet, TransactionType.WITHDRAWAL, amount);
        intraTransactionRepository.save(intraTransaction);

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

        InterTransaction interTransaction = new InterTransaction(senderWallet, recipientWallet, TransactionType.TRANSFER, amount);
        interTransactionRepository.save(interTransaction);

        return senderNewBalance;
    }

    public boolean isUserWalletOwner(Long userId, Long walletId) {
        Long walletIdFromUserId = walletRepository.findIdByUserId(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        return walletIdFromUserId.equals(walletId);
    }
}
