package com.example.wallet.repository;

import com.example.wallet.Enums.TransactionType;
import com.example.wallet.model.IntraTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IntraTransactionRepository extends JpaRepository<IntraTransaction, Long> {
    @Query("SELECT it FROM IntraTransaction it WHERE it.wallet.id = :walletId AND (:transactionType IS NULL OR it.type = :transactionType)")
    List<IntraTransaction> findByWalletIdAndTransactionType(Long walletId, TransactionType transactionType);
}
