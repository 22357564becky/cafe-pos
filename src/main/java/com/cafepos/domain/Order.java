package com.cafepos.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.cafepos.common.Money;

public final class Order {
    private final long id;
    private final List<LineItem> items = new ArrayList<>();

    public Order(long id) {
        this.id = id;
    }

    public void addItem(LineItem li) {
        if (li.quantity() < 0) throw new IllegalArgumentException("Negative amounts not allowed");
        items.add(li);
    }

    public Money subtotal() {
        return
                items.stream().map(LineItem::lineTotal).reduce(Money.zero()
                        , Money::add);
    }

    public Money taxAtPercent(int percent) {
        if (percent < 0) throw new IllegalArgumentException("Tax percent cannot be negative");
        BigDecimal taxRate = BigDecimal.valueOf(percent).divide(BigDecimal.valueOf(100));
        return subtotal().multiply(taxRate);
    }

    public Money totalWithTax(int percent) {
        return subtotal().add(taxAtPercent(percent));
    }

}