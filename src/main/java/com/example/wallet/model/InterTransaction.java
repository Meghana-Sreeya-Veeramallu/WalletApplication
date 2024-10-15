package com.example.wallet.model;

import com.example.wallet.Enums.TransactionType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inter_transactions")
public class InterTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_wallet_id", nullable = false)
    private Wallet senderWallet;

    @ManyToOne
    @JoinColumn(name = "recipient_wallet_id", nullable = false)
    private Wallet recipientWallet;

    @Enumerated(EnumType.STRING)
    private TransactionType type;
    private Double amount;
    private LocalDateTime timestamp;

    public InterTransaction() {}

    public InterTransaction(Wallet senderWallet, Wallet recipientWallet, TransactionType type, Double amount) {
        this.senderWallet = senderWallet;
        this.recipientWallet = recipientWallet;
        this.type = type;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }
}

