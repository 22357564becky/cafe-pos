package com.cafepos.observer;

import com.cafepos.domain.Order;

public final class DeliveryDesk implements OrderObserver {
    @Override
    public void updated(Order order, String eventType) {
        switch (eventType) {
            case "OrderPaid":
                System.out.println("[Payment] Order " + order.id() + " has been paid.");
                break;
            case "OrderReady":
                System.out.println("[Delivery] Order" + order.id() + " is ready for delivery.");
                break;
            default:
                System.out.println("Unknown event for order ID " + order.id() + ".");
        }
    }
}
