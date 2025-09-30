package com.cafepos.observer;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;

public final class KitchenDisplay implements OrderObserver {
    @Override
    public void updated(Order order, String eventType) {
        if(eventType.equals("ItemAdded")){
            LineItem lastItem = order.items().getLast();
            System.out.println("[Kitchen] Order #" + order.id() + ": " + lastItem.quantity() + "x " + lastItem.product().name() + " added");
        }
        else if(eventType.equals("OrderPaid")){
            System.out.println("[Kitchen] Order #"+order.id()+": payment received.");
        }
    }
    
}
