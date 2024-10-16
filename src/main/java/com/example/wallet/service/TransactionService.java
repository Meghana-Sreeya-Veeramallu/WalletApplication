package com.example.wallet.service;

import com.example.wallet.Enums.SortOrder;
import com.example.wallet.Enums.TransactionType;
import com.example.wallet.model.InterTransaction;
import com.example.wallet.model.IntraTransaction;
import com.example.wallet.repository.InterTransactionRepository;
import com.example.wallet.repository.IntraTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
    public List<Object> getTransactionHistory(Long walletId, SortOrder sortOrder, TransactionType transactionType) {
        List<IntraTransaction> intraTransactions = intraTransactionRepository.findByWalletIdAndTransactionType(walletId, transactionType);
        List<InterTransaction> sentTransactions = interTransactionRepository.findBySenderWalletIdAndTransactionType(walletId, transactionType);
        List<InterTransaction> receivedTransactions = interTransactionRepository.findByRecipientWalletIdAndTransactionType(walletId, transactionType);

        List<Object> allTransactions = new ArrayList<>();
        allTransactions.addAll(intraTransactions);
        allTransactions.addAll(sentTransactions);
        allTransactions.addAll(receivedTransactions);

        SortOrder order = (sortOrder != null) ? sortOrder : SortOrder.ASC;

        Comparator<Object> comparator = Comparator.comparing(transaction -> {
            if (transaction instanceof IntraTransaction) {
                return ((IntraTransaction) transaction).getTimestamp();
            } else if (transaction instanceof InterTransaction) {
                return ((InterTransaction) transaction).getTimestamp();
            }
            return null;
        });

        if (SortOrder.DESC.equals(order)) {
            comparator = comparator.reversed();
        }

        allTransactions.sort(comparator);

        return allTransactions;
    }
}
