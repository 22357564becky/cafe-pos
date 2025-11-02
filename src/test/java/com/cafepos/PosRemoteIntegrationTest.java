package com.cafepos;

import com.cafepos.command.AddItemCommand;
import com.cafepos.command.OrderService;
import com.cafepos.command.PayOrderCommand;
import com.cafepos.command.PosRemote;
import com.cafepos.common.Money;
import com.cafepos.domain.*;
import com.cafepos.payment.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PosRemoteIntegrationTest {

    @Test
    void remote_executes_commands_in_sequence_and_computes_expected_subtotal() {

        Order order = new Order(1);
        OrderService service = new OrderService(order);

        AddItemCommand addLatte = new AddItemCommand(service, "LAT", 1);
        AddItemCommand addScone = new AddItemCommand(service, "ESP", 1);
        PayOrderCommand pay = new PayOrderCommand(service, new CashPayment(), 10);

        PosRemote remote = new PosRemote(3);
        remote.setSlot(0, addLatte);
        remote.setSlot(1, addScone);
        remote.setSlot(2, pay);

        remote.press(0);
        remote.press(1);

        var subtotal = order.subtotal();
        assertEquals(Money.of(5.70), subtotal,
                "Subtotal should equal Latte (2.50) + Espresso (3.20) = 5.70");
    }
}
