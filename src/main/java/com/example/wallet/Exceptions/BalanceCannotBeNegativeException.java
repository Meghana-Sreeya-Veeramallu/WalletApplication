package com.example.wallet.Exceptions;

public class BalanceCannotBeNegativeException extends RuntimeException {
    public BalanceCannotBeNegativeException(String message) {
        super(message);
    }
}
