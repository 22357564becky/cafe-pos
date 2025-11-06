package com.cafepos;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.cafepos.common.Money;
import com.cafepos.menu.*;
import com.cafepos.state.OrderFSM;

public class Week9BehaviorTests {
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

    @Test
    void composite_iterator_depth_first_order_and_vegetarian_filter() {
        Menu root = new Menu("ROOT");
        Menu a = new Menu("A");
        Menu a1 = new Menu("A1");
        Menu b = new Menu("B");

        root.add(a);
        root.add(b);
        a.add(a1);
        a1.add(new MenuItem("a1", Money.of(1.00), true)); // vegetarian
        a.add(new MenuItem("a2", Money.of(1.50), false)); // non-veg
        b.add(new MenuItem("b1", Money.of(2.00), true)); // vegetarian

        List<String> names = root.allItems().stream().map(MenuComponent::name).toList();

        assertEquals(List.of("A", "A1", "a1", "a2", "B", "b1"), names);

        // vegetarianItems should return only the vegetarian MenuItems
        List<String> vegNames = root.vegetarianItems().stream().map(MenuItem::name).toList();
        assertEquals(List.of("a1", "b1"), vegNames);
    }

    @Test
    void order_fsm_legal_and_illegal_transitions_report() {
        OrderFSM fsm = new OrderFSM();
        assertEquals("NEW", fsm.status());

        var out = new ByteArrayOutputStream();
        var prev = System.out;
        System.setOut(new PrintStream(out));
        try {
            fsm.prepare();
            String printed = out.toString();
            assertTrue(printed.contains("Cannot prepare before pay") || printed.contains("Not ready yet"),
                    "Should inform about illegal prepare before pay");
            assertEquals("NEW", fsm.status(), "State should remain NEW after illegal prepare");

            out.reset();
            fsm.deliver();
            printed = out.toString();
            assertTrue(printed.contains("Cannot deliver yet") || printed.contains("Cannot deliver"),
                    "Should inform deliver not allowed before ready");
            assertEquals("NEW", fsm.status(), "State should remain NEW after illegal deliver");

            out.reset();
            fsm.pay(); // NEW -> PREPARING
            assertEquals("PREPARING", fsm.status());

            out.reset();
            fsm.markReady(); // PREPARING -> READY
            assertEquals("READY", fsm.status());

            out.reset();
            fsm.deliver(); // READY -> DELIVERED
            assertEquals("DELIVERED", fsm.status());

            out.reset();
            fsm.pay();
            printed = out.toString();
            assertTrue(printed.contains("Completed") || printed.contains("Already paid"),
                    "Pay after delivered should be a no-op message");
            assertEquals("DELIVERED", fsm.status());

        } finally {
            System.setOut(prev);
        }
    }
}
