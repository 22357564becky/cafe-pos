package com.cafepos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.cafepos.common.Money;
import com.cafepos.menu.*;
import com.cafepos.state.OrderFSM;

public class Week9Tests {
    @Test
    void depth_first_iteration_collects_all_nodes() {
        Menu root = new Menu("ROOT");
        Menu a = new Menu("A");
        Menu b = new Menu("B");
        root.add(a);
        root.add(b);
        a.add(new MenuItem("x", Money.of(1.0), true));
        b.add(new MenuItem("y", Money.of(2.0), false));
        List<String> names = root.allItems().stream().map(MenuComponent::name).toList();
        assertTrue(names.contains("x"));
        assertTrue(names.contains("y"));
    }

    @Test
    void order_fsm_happy_path() {
        OrderFSM fsm = new OrderFSM();
        assertEquals("NEW", fsm.status());
        fsm.pay();
        assertEquals("PREPARING", fsm.status());
        fsm.markReady();
        assertEquals("READY", fsm.status());
        fsm.deliver();
        assertEquals("DELIVERED", fsm.status());
    }
}
