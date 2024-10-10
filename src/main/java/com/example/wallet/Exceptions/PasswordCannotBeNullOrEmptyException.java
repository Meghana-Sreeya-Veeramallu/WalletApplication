package com.example.wallet.Exceptions;

public class PasswordCannotBeNullOrEmptyException extends RuntimeException {
    public PasswordCannotBeNullOrEmptyException(String message) {
        super(message);
    }
}
