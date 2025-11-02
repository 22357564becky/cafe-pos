package com.cafepos;

import com.cafepos.domain.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import com.cafepos.command.*;

class MacroCommandTest {

    @Test
    void macro_execute_and_undo_in_reverse_order() {
        Order order = new Order(1);
        OrderService service = new OrderService(order);

        AddItemCommand addLatte = new AddItemCommand(service, "LAT", 1);
        AddItemCommand addScone = new AddItemCommand(service, "ESP", 1);

        MacroCommand macro = new MacroCommand(addLatte, addScone);

        macro.execute();
        assertEquals(2, order.items().size(), "Two items should be added");

        macro.undo();
        assertEquals(0, order.items().size(), "Undo should reverse last addition first");
    }
}
