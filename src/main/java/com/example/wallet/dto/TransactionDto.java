package com.example.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {
    private String transactionType;
    private Long recipientWalletId;
    private Double amount;

    public TransactionDto(String transactionType, Double amount) {
        this.transactionType = transactionType;
        this.amount = amount;
    }
}
