package com.example.wallet.model;

import com.example.wallet.Enums.TransactionType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class IntraTransactionTest {

    @Test
    void testTransactionConstructor() throws Exception {
        Wallet wallet = new Wallet();
        TransactionType type = TransactionType.DEPOSIT;
        Double amount = 100.0;

        IntraTransaction intraTransaction = new IntraTransaction(wallet, type, amount);

        assertNotNull(intraTransaction);
    }
}
