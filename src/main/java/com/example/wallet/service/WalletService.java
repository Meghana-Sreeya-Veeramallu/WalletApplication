package com.example.wallet.service;

import com.example.wallet.Enums.TransactionType;
import com.example.wallet.Exceptions.UserNotAuthorizedException;
import com.example.wallet.Exceptions.UserNotFoundException;
import com.example.wallet.model.InterTransaction;
import com.example.wallet.model.IntraTransaction;
import com.example.wallet.model.User;
import com.example.wallet.model.Wallet;
import com.example.wallet.repository.InterTransactionRepository;
import com.example.wallet.repository.IntraTransactionRepository;
import com.example.wallet.repository.UserRepository;
import com.example.wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WalletService {
    private final WalletRepository walletRepository;
    private final IntraTransactionRepository intraTransactionRepository;
    private final InterTransactionRepository interTransactionRepository;
    private final UserRepository userRepository;

    @Autowired
    public WalletService(WalletRepository walletRepository, IntraTransactionRepository intraTransactionRepository, InterTransactionRepository interTransactionRepository, UserRepository userRepository) {
        this.walletRepository = walletRepository;
        this.intraTransactionRepository = intraTransactionRepository;
        this.interTransactionRepository = interTransactionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Double deposit(Long userId, Long walletId, Double amount) {
        if (!isUserAuthorized(userId, walletId)) {
            throw new UserNotAuthorizedException("Access denied: User is not authorized");
        }

        Wallet wallet = walletRepository.findById(walletId).orElseThrow(() -> new UserNotFoundException("User not found"));;
        Double newBalance = wallet.deposit(amount);
        walletRepository.save(wallet);

        IntraTransaction intraTransaction = new IntraTransaction(wallet, TransactionType.DEPOSIT, amount);
        intraTransactionRepository.save(intraTransaction);

        return newBalance;
    }

    @Transactional
    public Double withdraw(Long userId, Long walletId, Double amount) {
        if (!isUserAuthorized(userId, walletId)) {
            throw new UserNotAuthorizedException("Access denied: User is not authorized");
        }

        Wallet wallet = walletRepository.findById(walletId).orElseThrow(() -> new UserNotFoundException("User not found"));;
        Double newBalance = wallet.withdraw(amount);
        walletRepository.save(wallet);

        IntraTransaction intraTransaction = new IntraTransaction(wallet, TransactionType.WITHDRAWAL, amount);
        intraTransactionRepository.save(intraTransaction);

        return newBalance;
    }

    @Transactional
    public Double transfer(Long userId, Long senderWalletId, Long recipientWalletId, Double amount) {
        if (!isUserAuthorized(userId, senderWalletId)) {
            throw new UserNotAuthorizedException("Access denied: User is not authorized");
        }
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

    public boolean isUserAuthorized(Long userId, Long walletId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUsername = authentication.getName();
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        Long walletIdFromUserId = walletRepository.findIdByUserId(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        return (user.getUsername().equals(authenticatedUsername) && walletIdFromUserId.equals(walletId));
    }
}
