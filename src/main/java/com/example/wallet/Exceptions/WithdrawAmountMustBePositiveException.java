package com.example.wallet.Exceptions;

public class WithdrawAmountMustBePositiveException extends RuntimeException {
    public WithdrawAmountMustBePositiveException(String message) {
        super(message);
    }
}
