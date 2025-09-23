package com.cafepos;

import com.cafepos.catalog.*;
import com.cafepos.common.*;
import com.cafepos.domain.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PaymentStrategyTest {

    @Test
    void payment_strategy_called() {

        var product = new SimpleProduct("A", "A", Money.of(5.00));
        var order = new Order(42);
        order.addItem(new LineItem(product, 1));

        final boolean[] called = {false};
        PaymentStrategy fakeStrategy = o -> called[0] = true;

        order.pay(fakeStrategy);

        assertTrue(called[0], "Payment strategy should be called");
    }
}

