package com.example.wallet.repository;

import com.example.wallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long>  {
    @Query("SELECT w.id FROM Wallet w WHERE w.user.id = :userId")
    Optional<Long> findIdByUserId(@Param("userId") Long userId);
}
