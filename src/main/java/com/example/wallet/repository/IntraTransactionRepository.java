package com.example.wallet.repository;

import com.example.wallet.model.IntraTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IntraTransactionRepository extends JpaRepository<IntraTransaction, Long> {
    List<IntraTransaction> findByWalletId(Long walletId);
}
