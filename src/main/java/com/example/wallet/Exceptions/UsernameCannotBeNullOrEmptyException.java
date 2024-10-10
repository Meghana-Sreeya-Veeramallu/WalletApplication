package com.example.wallet.Exceptions;

public class UsernameCannotBeNullOrEmptyException extends RuntimeException {
    public UsernameCannotBeNullOrEmptyException(String message) {
        super(message);
    }
}
