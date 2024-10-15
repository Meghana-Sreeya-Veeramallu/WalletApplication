package com.example.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TransferDto {
    private Long senderId;
    private Long recipientId;
    private Double amount;
}
