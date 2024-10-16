package com.example.wallet.model;

import com.example.wallet.Enums.TransactionType;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "intra_transactions")
public class IntraTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Enumerated(EnumType.STRING)
    private TransactionType type;
    private Double amount;
    private LocalDateTime timestamp;

    public IntraTransaction() {}

    public IntraTransaction(Wallet wallet, TransactionType type, Double amount) {
        this.wallet = wallet;
        this.type = type;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }
}
