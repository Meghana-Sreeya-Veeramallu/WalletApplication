package com.example.wallet.repository;

import com.example.wallet.model.InterTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterTransactionRepository extends JpaRepository<InterTransaction, Long> {
    List<InterTransaction> findBySenderWalletId(Long senderWalletId);
    List<InterTransaction> findByRecipientWalletId(Long recipientWalletId);
}