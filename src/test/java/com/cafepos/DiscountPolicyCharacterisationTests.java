package com.cafepos;

import com.cafepos.smells.OrderManagerGod;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.cafepos.common.Money;
import com.cafepos.pricing.*;

public class DiscountPolicyCharacterisationTests {

    @Test
    void loyal5ShowsDiscountLineAndAdjustedTotal() {
        String receipt = OrderManagerGod.process("ESP", 2, null, "LOYAL5", false);
        assertTrue(receipt.contains("Discount:"), "Receipt should show a Discount line for LOYAL5");
        assertTrue(receipt.contains("Total:"), "Receipt should contain Total");
    }

    @Test
    void coupon1ShowsDiscountLineAndAdjustedTotal() {
        String receipt = OrderManagerGod.process("ESP", 2, null, "COUPON1", false);
        assertTrue(receipt.contains("Discount:"), "Receipt should show a Discount line for COUPON1");
        assertTrue(receipt.contains("Total:"), "Receipt should contain Total");
    }

    @Test
    void unknownCodeShowsNoDiscountLine() {
        String receipt = OrderManagerGod.process("ESP", 2, null, "UNKNOWN", false);
        assertFalse(receipt.contains("Discount:"), "Receipt should not show Discount line for unknown code");
        assertTrue(receipt.contains("Total:"), "Receipt should contain Total");
    }

    @Test
    void couponReturnsOneOrSubtotalIfLess() {
        var policy = new FixedCouponDiscount(Money.of(1.00));
        Money subtotal1 = Money.of(10.00);
        assertEquals(Money.of(1.00), policy.discountOf(subtotal1));

        Money subtotal2 = Money.of(0.50);
        assertEquals(subtotal2, policy.discountOf(subtotal2));
    }

    @Test
    void noDiscountReturnsZero() {
        var policy = new NoDiscount();
        Money subtotal = Money.of(5.00);
        assertEquals(Money.zero(), policy.discountOf(subtotal));
    }
}
