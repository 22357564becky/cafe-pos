package com.cafepos;
import org.junit.jupiter.api.Test;

import com.cafepos.catalog.*;
import com.cafepos.common.*;
import com.cafepos.decorator.*;
import com.cafepos.factory.*;
import com.cafepos.domain.*;


import static org.junit.jupiter.api.Assertions.*;

public class DecoratorTest {
    @Test
    void decorator_single_addOn(){
        Product espresso = new SimpleProduct("P-ESP","Espresso", Money.of(2.50));
        Product withShot = new ExtraShot(espresso);
        assertEquals("Espresso + Extra Shot", withShot.name());
        assertEquals(Money.of(3.30), ((Priced)withShot).price());
    }
    @Test
    void decorator_stacks(){
        Product espresso = new SimpleProduct("P-ESP","Espresso", Money.of(2.50));
        Product decorated = new SizeLarge(new OatMilk(new ExtraShot(espresso)));
        assertEquals("Espresso + Extra Shot + Oat Milk (Large)", decorated.name());
        assertEquals(Money.of(4.50), ((Priced)decorated).price());
    }
    @Test
    void factory_parses_recipe(){
        ProductFactory f = new ProductFactory();
        Product p = f.create("ESP+SHOT+OAT");
        assertTrue(p.name().contains("Espresso") && p.name().contains("Oat Milk"));
    }
    @Test
    void order_uses_decorated_price(){
        Product espresso = new SimpleProduct("P-ESP", "Espresso", Money.of(2.50));
        Product withShot = new ExtraShot(espresso);
        Order o = new Order(1);
        o.addItem(new LineItem(withShot, 2));
        assertEquals(Money.of(6.60), o.subtotal());
    } 
}
