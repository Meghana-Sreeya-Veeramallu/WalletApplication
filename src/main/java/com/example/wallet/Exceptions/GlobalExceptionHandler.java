package com.example.wallet.Exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameCannotBeNullOrEmptyException.class)
    public ResponseEntity<String> handleUsernameCannotBeNullOrEmpty(UsernameCannotBeNullOrEmptyException e) {
        return ResponseEntity.badRequest().body("Bad request: " + e.getMessage());
    }

    @ExceptionHandler(PasswordCannotBeNullOrEmptyException.class)
    public ResponseEntity<String> handlePasswordCannotBeNullOrEmpty(PasswordCannotBeNullOrEmptyException e) {
        return ResponseEntity.badRequest().body("Bad request: " + e.getMessage());
    }

    @ExceptionHandler(CurrencyCannotBeNullException.class)
    public ResponseEntity<String> handleCurrencyCannotBeNull(CurrencyCannotBeNullException e) {
        return ResponseEntity.badRequest().body("Bad request: " + e.getMessage());
    }

    @ExceptionHandler(UserNotAuthorizedException.class)
    public ResponseEntity<String> handleUserNotAuthorized() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: User is not authorized");
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

    @ExceptionHandler(DepositAmountMustBePositiveException.class)
    public ResponseEntity<String> handleDepositAmountMustBePositive(DepositAmountMustBePositiveException e) {
        return ResponseEntity.badRequest().body("Bad request: " + e.getMessage());
    }

    @ExceptionHandler(WithdrawAmountMustBePositiveException.class)
    public ResponseEntity<String> handleWithdrawAmountMustBePositive(WithdrawAmountMustBePositiveException e) {
        return ResponseEntity.badRequest().body("Bad request: " + e.getMessage());
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<String> handleInsufficientFunds(InsufficientFundsException e) {
        return ResponseEntity.badRequest().body("Bad request: " + e.getMessage());
    }

    @ExceptionHandler(TransferAmountMustBePositiveException.class)
    public ResponseEntity<String> handleTransferAmountMustBePositive(TransferAmountMustBePositiveException e) {
        return ResponseEntity.badRequest().body("Bad request: " + e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body("Bad request: " + e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        return ResponseEntity.badRequest().body("Bad Request: Username already exists");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception e) {
        return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
    }
}
