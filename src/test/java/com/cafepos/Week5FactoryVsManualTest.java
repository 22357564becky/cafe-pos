package com.cafepos;

import com.cafepos.factory.*;
import com.cafepos.catalog.*;
import com.cafepos.common.*;
import com.cafepos.decorator.*;
import com.cafepos.domain.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Week5FactoryVsManualTest {
    @Test
    void testFactoryVsManualDrinkEquality() {

        Product viaFactory = new ProductFactory().create("ESP+SHOT+OAT+L");
        Product viaManual = new SizeLarge(
                new OatMilk(
                        new ExtraShot(
                                new SimpleProduct("P-ESP", "Espresso", Money.of(2.50))
                        )
                )
        );
        assertEquals(viaFactory.name(), viaManual.name(), "Names should match");

        if (viaFactory instanceof Priced && viaManual instanceof Priced) {
            Money priceFactory = ((Priced) viaFactory).price();
            Money priceManual = ((Priced) viaManual).price();
            assertEquals(priceFactory, priceManual, "Unit prices should match");
        }

        Order orderFactory = new Order(OrderIds.next());
        orderFactory.addItem(new LineItem(viaFactory, 1));
        Order orderManual = new Order(OrderIds.next());
        orderManual.addItem(new LineItem(viaManual, 1));

        assertEquals(orderFactory.subtotal(), orderManual.subtotal(), "Subtotals should match");
        assertEquals(orderFactory.totalWithTax(10), orderManual.totalWithTax(10), "Totals with tax should match");
    }
}
