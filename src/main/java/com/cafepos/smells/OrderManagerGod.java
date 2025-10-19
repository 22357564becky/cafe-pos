package com.cafepos.smells;

import com.cafepos.common.Money;
import com.cafepos.factory.ProductFactory;
import com.cafepos.pricing.*;
import com.cafepos.catalog.Product;
import com.cafepos.payment.*;
import com.cafepos.domain.*;

public class OrderManagerGod {

    //Instance fields instead of globals
    private final int taxPercent;
    private String lastDiscountCode;

    private final ProductFactory productFactory;
    private final ReceiptPrinter printer;

    public OrderManagerGod(int taxPercent) {
        this.taxPercent = taxPercent;
        this.productFactory = new ProductFactory();
        this.printer = new ReceiptPrinter();
    }

    //no longer one whole method
    public static String process(String recipe, int qty, String paymentType, String discountCode,
                                 boolean printReceipt) {
        // Delegate to instance with default dependencies
        OrderManagerGod manager = new OrderManagerGod(10); // Default tax 10%
        return manager.processOrder(recipe, qty, paymentType, discountCode, printReceipt);
    }

    private String processOrder(String recipe, int qty, String paymentType, String discountCode,
                                boolean printReceipt) {
        Product product = productFactory.create(recipe);
        Money unitPrice;

        // knows too much about payment strategy types - feature envy smell
        try {
            unitPrice = product instanceof com.cafepos.decorator.Priced p ? p.price() : product.basePrice();

        } catch (Exception e) {
            unitPrice = product.basePrice();
        }

        // magic number and inline logic - primitive obsession smell
        if (qty <= 0)
            qty = 1;

        Money subtotal = unitPrice.multiply(java.math.BigDecimal.valueOf(qty));

        Money discount = calculateDiscount(discountCode, subtotal);

        // duplicated logic -the same subtraction pattern could be extracted
        Money discounted = Money.of(subtotal.asBigDecimal().subtract(discount.asBigDecimal()));
        if (discounted.asBigDecimal().signum() < 0)
            discounted = Money.zero();

        FixedRateTaxPolicy taxPolicy = new FixedRateTaxPolicy(taxPercent);
        Money tax = taxPolicy.taxOn(discounted);
        Money total = discounted.add(tax);

        // delegate payment handling to PaymentStrategy implementations
        if (paymentType != null) {
            PaymentStrategy strategy = null;
            if (paymentType.equalsIgnoreCase("CASH")) {
                strategy = new CashPayment();
            } else if (paymentType.equalsIgnoreCase("CARD")) {
                // previously printed masked card ****1234 - supply a placeholder card number
                strategy = new CardPayment("00001234");
            } else if (paymentType.equalsIgnoreCase("WALLET")) {
                strategy = new WalletPayment("user-wallet-789");
            } else {
                System.out.println("[UnknownPayment] " + total);
            }

            if (strategy != null) {
                Order orderToPay = new Order(OrderIds.next());
                orderToPay.addItem(new LineItem(product, qty));
                orderToPay.pay(strategy);
            }
        }
        //extract receipt printer
        PricingService.PricingResult pr = new PricingService.PricingResult(subtotal, discount, tax, total);
        String out = printer.format(recipe, qty, pr, taxPercent);

        if (printReceipt) {
            System.out.println(out);
        }
        return out;
    }

    // extracted discount calculation to reduce method complexity and duplication
    private Money calculateDiscount(String discountCode, Money subtotal) {
        DiscountPolicy policy;
        if ("LOYAL5".equalsIgnoreCase(discountCode)) {
            policy = new LoyaltyPercentDiscount(5);
        } else if ("COUPON1".equalsIgnoreCase(discountCode)) {
            policy = new FixedCouponDiscount(Money.of(1.00));
        } else {
            policy = new NoDiscount();
        }

        // preserving previous discount behaviour
        this.lastDiscountCode = discountCode;
        return policy.discountOf(subtotal);
    }

    public String getLastDiscountCode() {
        return lastDiscountCode;
    }
}