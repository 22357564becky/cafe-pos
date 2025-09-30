package com.cafepos.observer;
import com.cafepos.domain.Order;

public class CustomerNotifier implements OrderObserver {
    @Override
    public void updated(Order order, String eventType) {
        if ("ItemAdded".equals(eventType) || "OrderReady".equals(eventType)) {
            System.out.println("[Customer] Dear customer, your Order #" + order.id() + " has been updated: " + eventType+ ".");
        }
    }
}
