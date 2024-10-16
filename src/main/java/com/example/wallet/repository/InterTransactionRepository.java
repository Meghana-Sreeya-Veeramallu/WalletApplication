package com.example.wallet.repository;

import com.example.wallet.Enums.TransactionType;
import com.example.wallet.model.InterTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InterTransactionRepository extends JpaRepository<InterTransaction, Long> {
    @Query("SELECT it FROM InterTransaction it WHERE it.senderWallet.id = :senderWalletId AND (:transactionType IS NULL OR it.type = :transactionType)")
    List<InterTransaction> findBySenderWalletIdAndTransactionType(Long senderWalletId, TransactionType transactionType);

    @Query("SELECT it FROM InterTransaction it WHERE it.recipientWallet.id = :recipientWalletId AND (:transactionType IS NULL OR it.type = :transactionType)")
    List<InterTransaction> findByRecipientWalletIdAndTransactionType(Long recipientWalletId, TransactionType transactionType);
}