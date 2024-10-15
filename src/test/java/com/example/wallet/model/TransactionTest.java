package com.example.wallet.model;

import com.example.wallet.Enums.TransactionType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TransactionTest {

    @Test
    void testTransactionConstructor() throws Exception {
        Wallet wallet = new Wallet();
        TransactionType type = TransactionType.DEPOSIT;
        Double amount = 100.0;

        Transaction transaction = new Transaction(wallet, type, amount);

        assertNotNull(transaction);
    }
}
