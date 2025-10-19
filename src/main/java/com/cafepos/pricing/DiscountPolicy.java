package com.cafepos.pricing;

import com.cafepos.common.*;

public interface DiscountPolicy {
    Money discountOf(Money subtotal);
}
