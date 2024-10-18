package com.example.wallet.Exceptions;

public class InvalidTransactionTypeException extends RuntimeException {
    public InvalidTransactionTypeException(String message) {
        super(message);
    }
}
