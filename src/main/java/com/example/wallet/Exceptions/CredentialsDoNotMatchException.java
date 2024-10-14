package com.example.wallet.Exceptions;

public class CredentialsDoNotMatchException extends RuntimeException {
    public CredentialsDoNotMatchException(String message) {
        super(message);
    }
}
