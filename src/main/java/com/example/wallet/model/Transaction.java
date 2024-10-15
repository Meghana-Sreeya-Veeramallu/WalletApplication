package com.example.wallet.model;

import com.example.wallet.Enums.TransactionType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {
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

    public Transaction() {}

    public Transaction(Wallet wallet, TransactionType type, Double amount) {
        this.wallet = wallet;
        this.type = type;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }
}
