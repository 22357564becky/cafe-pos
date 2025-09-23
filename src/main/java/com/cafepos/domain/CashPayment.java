package com.cafepos.domain;

public final class CashPayment implements PaymentStrategy {
    @Override
    public void pay(Order order) {
        System.out.println("Paid " + order.totalWithTax(10) + " in cash for order " + order.id());
    }
    
}
