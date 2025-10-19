package com.cafepos.pricing;

import com.cafepos.common.*;

public final class NoDiscount implements DiscountPolicy {
    @Override
    public Money discountOf(Money subtotal) {
        return Money.zero();
    }
}