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

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final IntraTransactionRepository intraTransactionRepository;
    private final InterTransactionRepository interTransactionRepository;

    @Autowired
    public TransactionService(UserRepository userRepository, WalletRepository walletRepository, IntraTransactionRepository intraTransactionRepository, InterTransactionRepository interTransactionRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.intraTransactionRepository = intraTransactionRepository;
        this.interTransactionRepository = interTransactionRepository;
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

    private boolean isUserAuthorized(Long userId, Long walletId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUsername = authentication.getName();
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        Long walletIdFromUserId = walletRepository.findIdByUserId(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        return (user.getUsername().equals(authenticatedUsername) && walletIdFromUserId.equals(walletId));
    }

    @Transactional(readOnly = true)
    public List<Object> getTransactionHistory(Long userId, Long walletId, String sortBy, String sortOrder, String transactionType) {
        if (!isUserAuthorized(userId, walletId)) {
            throw new UserNotAuthorizedException("Access denied: User is not authorized");
        }

        List<String> sortOrderList = sortOrder != null ? Arrays.asList(sortOrder.split(",")) : Collections.emptyList();
        List<String> sortByList = sortBy != null ? Arrays.asList(sortBy.split(",")) : Collections.emptyList();
        List<String> transactionTypeList = transactionType != null ? Arrays.asList(transactionType.split(",")) : Collections.emptyList();
        validateSortParameters(sortByList, sortOrderList, transactionTypeList);

        List<IntraTransaction> intraTransactions = intraTransactionRepository.findByWalletId(walletId);
        List<InterTransaction> interTransactions = interTransactionRepository.findByWalletId(walletId);

        List<Object> allTransactions = new ArrayList<>();
        allTransactions.addAll(intraTransactions);
        allTransactions.addAll(interTransactions);

        if (transactionType != null) {
            allTransactions = allTransactions.stream()
                    .filter(transaction -> transactionTypeList.contains(getTransactionType(transaction)))
                    .collect(Collectors.toList());
        }

        Comparator<Object> comparator = getComparatorObject(sortOrderList, sortByList);

        allTransactions.sort(comparator);
        return allTransactions;
    }

    private void validateSortParameters(List<String> sortByList, List<String> sortOrderList, List<String> transactionTypeList) {
        if (sortByList.size() < sortOrderList.size()) {
            throw new IllegalArgumentException("The number of sort fields must be greater than or equal to the number of sort orders");
        }

        List<String> validSortFields = Arrays.asList("timestamp", "amount");
        for (String field : sortByList) {
            if (!validSortFields.contains(field.toLowerCase())) {
                throw new IllegalArgumentException("Invalid sort field: " + field);
            }
        }

        List<String> validSortOrders = Arrays.asList("ASC", "DESC");
        for (String order : sortOrderList) {
            if (!validSortOrders.contains(order.toUpperCase())) {
                throw new IllegalArgumentException("Invalid sort order: " + order);
            }
        }

        List<String> validTransactionTypes = Arrays.asList("WITHDRAWAL", "TRANSFER", "DEPOSIT");
        for (String type : transactionTypeList) {
            if (!validTransactionTypes.contains(type.toUpperCase())) {
                throw new IllegalArgumentException("Invalid transaction type: " + type);
            }
        }
    }

    private String getTransactionType(Object transaction) {
        if (transaction instanceof IntraTransaction) {
            return ((IntraTransaction) transaction).getType().name();
        } else {
            return ((InterTransaction) transaction).getType().name();
        }
    }

    private Comparator<Object> getComparatorObject(List<String> sortOrderList, List<String> sortByList) {
        Comparator<Object> comparator = (transaction1, transaction2) -> {
            int comparisonResult = 0;
            for (int i = 0; i < sortByList.size(); i++) {
                String sortByField = sortByList.get(i);
                boolean isDesc = (sortOrderList.size() > i && "DESC".equalsIgnoreCase(sortOrderList.get(i)));

                if (sortByField.equalsIgnoreCase("timestamp")) {
                    comparisonResult = compareByTimestamp(transaction1, transaction2);
                } else if (sortByField.equalsIgnoreCase("amount")) {
                    comparisonResult = compareByAmount(transaction1, transaction2);
                }
                if (comparisonResult != 0) {
                    return isDesc ? -comparisonResult : comparisonResult;
                }
            }
            return comparisonResult;
        };
        return comparator;
    }

    private int compareByTimestamp(Object transaction1, Object transaction2) {
        LocalDateTime timestamp1 = getTransactionValue(transaction1, IntraTransaction::getTimestamp, InterTransaction::getTimestamp);
        LocalDateTime timestamp2 = getTransactionValue(transaction2, IntraTransaction::getTimestamp, InterTransaction::getTimestamp);
        return timestamp1.compareTo(timestamp2);
    }

    private int compareByAmount(Object transaction1, Object transaction2) {
        Double amount1 = getTransactionValue(transaction1, IntraTransaction::getAmount, InterTransaction::getAmount);
        Double amount2 = getTransactionValue(transaction2, IntraTransaction::getAmount, InterTransaction::getAmount);
        return amount1.compareTo(amount2);
    }

    private <T> T getTransactionValue(Object transaction, Function<IntraTransaction, T> intraMapper, Function<InterTransaction, T> interMapper) {
        if (transaction instanceof IntraTransaction) {
            return intraMapper.apply((IntraTransaction) transaction);
        } else {
            return interMapper.apply((InterTransaction) transaction);
        }
    }
}