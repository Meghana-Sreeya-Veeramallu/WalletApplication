package com.example.wallet.model;

import com.example.wallet.Enums.TransactionType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InterTransactionTest {
    @Test
    void testTransactionConstructor() throws Exception {
        Wallet senderWallet = new Wallet();
        Wallet recipientWallet = new Wallet();
        TransactionType type = TransactionType.DEPOSIT;
        Double amount = 100.0;

        InterTransaction interTransaction = new InterTransaction(senderWallet, recipientWallet, type, amount);

        assertNotNull(interTransaction);
    }
}