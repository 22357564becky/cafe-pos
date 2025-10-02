package com.cafepos;
import org.junit.jupiter.api.Test;

import com.cafepos.catalog.*;
import com.cafepos.domain.*;
import com.cafepos.common.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ObserverTest {
    @Test void observers_notified_on_item_add() {
        var p = new SimpleProduct("A", "A", Money.of(2));
        var o = new Order(1);
        o.addItem(new LineItem(p, 1));

        List<String> events = new ArrayList<>();
        o.register((order, evt) -> events.add(evt));

        o.addItem(new LineItem(p, 1));
        assertTrue(events.contains("ItemAdded"));
    }

    @Test
    public void order_paid_event() {
        var o = new Order(1);
        List<String> events = new ArrayList<>();

        o.register((order, eventType) -> events.add(eventType));
        o.pay(order -> {});

        assertTrue(events.contains("OrderPaid"));
    }



    @Test
    void mark_ready_notifies_observers() {
        var o = new Order(1);
        List<String> events = new ArrayList<>();

        o.register((order, eventType) -> events.add(eventType));

        o.markReady();

        assertTrue(events.contains("OrderReady"));
    }
    
}
