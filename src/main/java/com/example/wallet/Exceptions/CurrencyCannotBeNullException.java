package com.example.wallet.Exceptions;

public class CurrencyCannotBeNullException extends RuntimeException {
    public CurrencyCannotBeNullException(String message) {
        super(message);
    }
}
