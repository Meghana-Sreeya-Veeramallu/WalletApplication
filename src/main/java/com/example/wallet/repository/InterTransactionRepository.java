package com.example.wallet.repository;

import com.example.wallet.Enums.TransactionType;
import com.example.wallet.model.InterTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InterTransactionRepository extends JpaRepository<InterTransaction, Long> {
    @Query("SELECT it FROM InterTransaction it WHERE (it.senderWallet.id = :walletId OR it.recipientWallet.id = :walletId) AND (:transactionType IS NULL OR it.type = :transactionType)")
    List<InterTransaction> findByWalletIdAndTransactionType(Long walletId, TransactionType transactionType);
}