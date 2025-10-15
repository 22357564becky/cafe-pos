package com.cafepos.payment;

import com.cafepos.domain.Order;

public final class WalletPayment implements PaymentStrategy {
    private final String walletId;

    public WalletPayment(String walletId) {
        if (walletId == null || walletId.isEmpty()) {
            throw new IllegalArgumentException("Wallet ID is required");
        }  
        
        this.walletId = walletId;
    }

    @Override    
    public void pay(Order order) {
        String namePart = walletId.split("-wallet-id")[0];
        String fullWalletId = namePart + "-wallet-id";
        if (namePart.isEmpty()) {
            throw new IllegalArgumentException("Invalid wallet ID format");
        }

        System.out.println("[Wallet] Customer paid " + order.totalWithTax(10) + " EUR via wallet " + fullWalletId);    
    }
    
}
