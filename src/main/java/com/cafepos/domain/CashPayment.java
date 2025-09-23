package com.cafepos.domain;

public final class CashPayment implements PaymentStrategy {
    @Override
    public void pay(Order order) {
        System.out.println("[Cash] Customer paid " + order.totalWithTax(10) + " EUR");
    }
    
}
