package com.example.wallet.service;

import com.example.wallet.model.InterTransaction;
import com.example.wallet.model.IntraTransaction;
import com.example.wallet.repository.InterTransactionRepository;
import com.example.wallet.repository.IntraTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    public List<Object> getTransactionHistory(Long walletId) {
        List<IntraTransaction> intraTransactions = intraTransactionRepository.findByWalletId(walletId);
        List<InterTransaction> sentTransactions = interTransactionRepository.findBySenderWalletId(walletId);
        List<InterTransaction> receivedTransactions = interTransactionRepository.findByRecipientWalletId(walletId);

        List<Object> allTransactions = new ArrayList<>();
        allTransactions.addAll(intraTransactions);
        allTransactions.addAll(sentTransactions);
        allTransactions.addAll(receivedTransactions);

        return allTransactions;
    }
}
