package com.example.wallet.service;

import com.example.wallet.Enums.TransactionType;
import com.example.wallet.model.InterTransaction;
import com.example.wallet.model.IntraTransaction;
import com.example.wallet.repository.InterTransactionRepository;
import com.example.wallet.repository.IntraTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

@Service
public class TransactionService {
    private final IntraTransactionRepository intraTransactionRepository;
    private final InterTransactionRepository interTransactionRepository;

    @Autowired
    public TransactionService(IntraTransactionRepository intraTransactionRepository, InterTransactionRepository interTransactionRepository) {
        this.intraTransactionRepository = intraTransactionRepository;
        this.interTransactionRepository = interTransactionRepository;
    }

    @Transactional(readOnly = true)
    public List<Object> getTransactionHistory(Long walletId, String sortBy, String sortOrder, TransactionType transactionType) {
        List<String> sortOrderList = sortOrder != null ? Arrays.asList(sortOrder.split(",")) : Collections.emptyList();
        List<String> sortByList = sortBy != null ? Arrays.asList(sortBy.split(",")) : Collections.emptyList();
        validateSortParameters(sortByList, sortOrderList);

        List<IntraTransaction> intraTransactions = intraTransactionRepository.findByWalletIdAndTransactionType(walletId, transactionType);
        List<InterTransaction> interTransactions = interTransactionRepository.findByWalletIdAndTransactionType(walletId, transactionType);

        List<Object> allTransactions = new ArrayList<>();
        allTransactions.addAll(intraTransactions);
        allTransactions.addAll(interTransactions);

        Comparator<Object> comparator = getComparatorObject(sortOrderList, sortByList);

        allTransactions.sort(comparator);
        return allTransactions;
    }

    private void validateSortParameters(List<String> sortByList, List<String> sortOrderList) {
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
        } else if (transaction instanceof InterTransaction) {
            return interMapper.apply((InterTransaction) transaction);
        }
        throw new IllegalArgumentException("Unknown transaction type");
    }
}