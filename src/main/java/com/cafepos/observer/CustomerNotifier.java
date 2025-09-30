package com.cafepos.observer;
import com.cafepos.domain.Order;

public class CustomerNotifier implements OrderObserver {
    @Override
    public void updated(Order order, String eventType) {
        
    }
}
