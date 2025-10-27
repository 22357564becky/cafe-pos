package com.cafepos.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.cafepos.common.Money;
import com.cafepos.observer.OrderObserver;
import com.cafepos.payment.PaymentStrategy;

public final class Order implements OrderPublisher {
    private final long id;
    private final List<LineItem> items = new ArrayList<>();
    private final List<OrderObserver> observers = new ArrayList<>();

    public Order(long id) {
        this.id = id;
    }

    public List<LineItem> items() {
        return List.copyOf(items);
    }

    public void addItem(LineItem li) {
        if (li.quantity() < 0) throw new IllegalArgumentException("Negative amounts not allowed");
        items.add(li);
        notifyObservers(this, "ItemAdded");
    }

    public Money subtotal() {
        return items.stream().map(LineItem::lineTotal).reduce(Money.zero(), Money::add);
    }

    public Money taxAtPercent(int percent) {
        if (percent < 0) throw new IllegalArgumentException("Tax percent cannot be negative");
        BigDecimal taxRate = BigDecimal.valueOf(percent).divide(BigDecimal.valueOf(100));
        return subtotal().multiply(taxRate);
    }

    public Money totalWithTax(int percent) {
        return subtotal().add(taxAtPercent(percent));
    }

    public long id() {
        return id;
    }

    public void pay(PaymentStrategy strategy) {
        if (strategy == null) throw new
                IllegalArgumentException("strategy required");
        strategy.pay(this);
        notifyObservers(this, "OrderPaid");
    }

    public void register(OrderObserver o) {
        if (o == null) throw new IllegalArgumentException("Observer cannot be null");
        observers.add(o);
    }

    public void unregister(OrderObserver o) {
        observers.remove(o);
    }
    
    public void notifyObservers(Order order, String eventType) {
        for (OrderObserver o : observers) {
            o.updated(order, eventType);
        }
    }

    public void markReady() {
        notifyObservers(this, "OrderReady");
    }


}