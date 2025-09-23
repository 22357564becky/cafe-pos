package com.cafepos.domain;

public final class WalletPayment {
    private final String walletId;

    public WalletPayment(String walletId) {
        this.walletId = walletId;
    }

    public void pay(Order order) {
        System.out.println("Paid " + order.totalWithTax(10) + " using wallet " + walletId + " for order " + order.id());
    }
    
}
