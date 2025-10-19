package com.cafepos;

import com.cafepos.catalog.*;
import com.cafepos.common.*;
import com.cafepos.domain.*;
import com.cafepos.payment.*;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PaymentStrategyTest {

    @Test
    void payment_strategy_called() {

        var product = new SimpleProduct("A", "A", Money.of(5.00));
        var order = new Order(42);
        order.addItem(new LineItem(product, 1));

        final boolean[] called = { false };
        PaymentStrategy fakeStrategy = o -> called[0] = true;

        order.pay(fakeStrategy);

        assertTrue(called[0], "Payment strategy should be called");
    }

    @Test
    void wallet_payment_strategy() {

        var product = new SimpleProduct("A", "A", Money.of(5.00));
        var order = new Order(42);
        order.addItem(new LineItem(product, 1));

        PaymentStrategy walletStrategy = new WalletPayment("alice-wallet-id");

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        order.pay(walletStrategy);

        Money expectedTotal = order.totalWithTax(10);
        String expectedOutput = "[Wallet] Customer paid " + expectedTotal + " EUR via wallet alice-wallet-id"
                + System.lineSeparator();
        assertEquals(expectedOutput, outContent.toString());
    }

}
