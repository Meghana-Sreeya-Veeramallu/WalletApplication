package com.example.wallet.Exceptions;

public class AmountCannotBeNullException extends RuntimeException {
    public AmountCannotBeNullException(String message) {
        super(message);
    }
}
