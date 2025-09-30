package com.cafepos.observer;
import com.cafepos.domain.Order;

public final class Kitchen implements OrderObserver {
    @Override
    public void updated(Order order, String eventType) {
        if(eventType.equals("ItemAdded")){
            System.out.println("[Kitchen] id "+order.id()+" item added.");
        }
        else if(eventType.equals("OrderPaid")){
            System.out.println("[Kitchen] order "+order.id()+" payment received.");
        }
        else{
            System.out.println("Unknown event for order ID " + order.id() + ".");
        }
    }
    
}
