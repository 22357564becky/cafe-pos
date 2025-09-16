package com.cafepos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    void testAddition() {
        Money m1 = Money.of(5.00);
        Money m2 = Money.of(3.00);
        Money result = m1.add(m2);
        assertEquals(Money.of(5.00), result);
    }

    @Test
    void testMultiplication() {
        Money m = Money.of(1.25);
        Money result = m.multiply(3);
        assertEquals(Money.of(3.75), result);
    }

    @Test
    void testZero() {
        assertEquals(Money.of(0.00), Money.zero());
    }

    @Test
    void testNoNegativeAmountAllowed() {
        assertThrows(IllegalArgumentException.class, () -> {
            Money.of(-1.00);
        });
    }

    @Test
    void testMultiplyByNegativeThrows() {
        Money m = Money.of(2.00);
        assertThrows(IllegalArgumentException.class, () -> {
            m.multiply(-2);
        });
    }

    @Test
    void testEqualsAndHashCode() {
        Money a = Money.of(2.00);
        Money b = Money.of(2.00);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testCompareTo() {
        Money a = Money.of(1.00);
        Money b = Money.of(2.00);
        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);
        assertEquals(0, a.compareTo(Money.of(1.00)));
    }

    @Test
    void testToString() {
        Money m = Money.of(4.50);
        assertEquals("4.50", m.toString());
    }

    @Test
    void testRoundUp() {
        Money m = Money.of(2.005);
        assertEquals(Money.of(2.01), m);
    }
}
