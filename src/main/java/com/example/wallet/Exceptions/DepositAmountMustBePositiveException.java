package com.example.wallet.Exceptions;

public class DepositAmountMustBePositiveException extends RuntimeException {
    public DepositAmountMustBePositiveException(String message) {
        super(message);
    }
}
