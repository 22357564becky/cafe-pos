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
    
}
