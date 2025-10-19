package com.cafepos.smells;

import com.cafepos.common.Money;
import com.cafepos.factory.ProductFactory;
import com.cafepos.pricing.*;
// import com.cafepos.pricing.*; // not used
import com.cafepos.catalog.Product;

public class OrderManagerGod {

    // global state smell - hard to test, shaed mutable state
    public static int TAX_PERCENT = 10;
    public static String LAST_DISCOUNT_CODE = null;

    // one big method does it all - long method smell/god class smell
    public static String process(String recipe, int qty, String paymentType, String discountCode,
                                 boolean printReceipt) {
        ProductFactory factory = new ProductFactory();
        Product product = factory.create(recipe);
        Money unitPrice;

        // knows too much about payment strategy types - feature envy smell
        try {
            var priced = product instanceof com.cafepos.decorator.Priced p ? p.price() : product.basePrice();
            unitPrice = priced;
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
        
        FixedRateTaxPolicy taxPolicy = new FixedRateTaxPolicy(TAX_PERCENT);
        Money tax = taxPolicy.taxOn(discounted);
        Money total = discounted.add(tax);

        // payment logic should be delegated - feature envy smell
        if (paymentType != null) {
            // string comparisons - primitive obsession smell
            if (paymentType.equalsIgnoreCase("CASH")) {
                System.out.println("[Cash] Customer paid " + total + " EUR");
            } else if (paymentType.equalsIgnoreCase("CARD")) {
                System.out.println("[Card] Customer paid " + total + " EUR with card ****1234");
            } else if (paymentType.equalsIgnoreCase("WALLET")) {
                System.out.println("[Wallet] Customer paid " + total + " EUR via wallet user-wallet-789");
            } else {
                System.out.println("[UnknownPayment] " + total);
            }
        }

        // business logic mixed with presentation logic - separation of concerns smell
        StringBuilder receipt = new StringBuilder();
        receipt.append("Order (").append(recipe).append(") x").append(qty).append("\n");
        receipt.append("Subtotal: ").append(subtotal).append("\n");
        if (discount.asBigDecimal().signum() > 0) {
            receipt.append("Discount: -").append(discount).append("\n");
        }
        receipt.append("Tax (").append(TAX_PERCENT).append("%): ").append(tax).append("\n");
        receipt.append("Total: ").append(total);

        String out = receipt.toString();
        // I/O operation mixed in with business logic
        if (printReceipt) {
            System.out.println(out);
        }

        return out;
    }

    // extracted discount calculation to reduce method complexity and duplication
    private static Money calculateDiscount(String discountCode, Money subtotal) {
        DiscountPolicy policy;
        if ("LOYAL5".equalsIgnoreCase(discountCode)) {
            policy = new LoyaltyPercentDiscount(5);
        } else if ("COUPON1".equalsIgnoreCase(discountCode)) {
            policy = new FixedCouponDiscount(Money.of(1.00));
        } else {
            policy = new NoDiscount();
        }

        // preserving previous discount behaviour
        LAST_DISCOUNT_CODE = discountCode;
        return policy.discountOf(subtotal);
    }
}