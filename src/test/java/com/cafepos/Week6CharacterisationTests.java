package com.cafepos;

import com.cafepos.smells.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Week6CharacterisationTests {
    @Test
    void no_discount_cash_payment(){
        String recipt = OrderManagerGod.process("ESP+SHOT+OAT", 1, "CASH", "NONE", false);
        assertTrue(recipt.contains("Subtotal: 3.80"));
        assertTrue(recipt.contains("Tax (10%): 0.38"));
        assertTrue(recipt.contains("Total: 4.18"));
    }

    @Test
    void loyalty_discount_card_payment(){
        String recipt = OrderManagerGod.process("LAT+L", 2, "CARD", "LOYAL5", false);
        // Latte (Large) base = 3.20 + 0.70 = 3.90, qty 2 => 7.80
        // 5% discount => 0.39, discounted=7.41; tax 10% => 0.74; total=8.15
        assertTrue(recipt.contains("Subtotal: 7.80"));
        assertTrue(recipt.contains("Discount: -0.39"));
        assertTrue(recipt.contains("Tax (10%): 0.74"));
        assertTrue(recipt.contains("Total: 8.15"));

    }

    @Test
    void coupon_fixed_amount_and_qty_clamp(){
        String recipt = OrderManagerGod.process("ESP+SHOT", 0, "WALLET", "COUPON1", false);
        //qty=0 clamped to 1; Espresso+SHOT = 2.50 + 0.80 = 3.30; coupon1 => -1.00 => 2.30; tax=0.23; total=2.53
        assertTrue(recipt.contains("Order (ESP+SHOT)x1"));
        assertTrue(recipt.contains("Subtotal: 3.30"));
        assertTrue(recipt.contains("Discount: -1.00"));
        assertTrue(recipt.contains("Tax (10%): 0.23"));
        assertTrue(recipt.contains("Total: 2.53"));
    }
    

}
