package com.cafepos;

import com.cafepos.command.AddItemCommand;
import com.cafepos.command.OrderService;
import com.cafepos.domain.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddItemCommandTest {

    @Test
    void execute_undo() {
        Order order = new Order(1);
        OrderService service = new OrderService(order);

        AddItemCommand cmd = new AddItemCommand(service, "LAT", 1);

        int before = order.items().size();

        cmd.execute();
        int afterExecute = order.items().size();
        assertEquals(before + 1, afterExecute, "Item should be added after execute");

        cmd.undo();
        int afterUndo = order.items().size();
        assertEquals(before, afterUndo, "Undo should remove the added item");
    }

}
