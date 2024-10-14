package com.example.wallet.Exceptions;

public class TransferAmountMustBePositiveException extends RuntimeException {
    public TransferAmountMustBePositiveException(String message) {
        super(message);
    }
}
