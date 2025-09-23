package com.cafepos.domain;

public interface PaymentStrategy {
    void pay(Order order);
    
}
