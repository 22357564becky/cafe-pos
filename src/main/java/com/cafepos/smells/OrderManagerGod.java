package com.cafepos.smells;

import com.cafepos.common.Money;
import com.cafepos.factory.ProductFactory;
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
        Money subtotal = unitPrice.multiply(qty);

        Money discount = Money.zero();
        // using strings for busines logic - primitive obsession smell
        if (discountCode != null) {
            // duplicated logic -the same bigDecimal maths is repeated
            if (discountCode.equalsIgnoreCase("LOYAL5")) {
                // duplicated logic -the same bigDecimal maths is repeated
                discount = Money.of(subtotal.asBigDecimal()
                        .multiply(java.math.BigDecimal.valueOf(5))
                        .divide(java.math.BigDecimal.valueOf(100)));
            } else if (discountCode.equalsIgnoreCase("COUPON1")) {
                discount = Money.of(1.00);
            } else if (discountCode.equalsIgnoreCase("NONE")) {
                discount = Money.zero();
            } else {
                discount = Money.zero();
            }
            // global state mutation smell
            LAST_DISCOUNT_CODE = discountCode;
        }

        // duplicated logic -the same subtraction pattern could be extracted
        Money discounted = Money.of(subtotal.asBigDecimal().subtract(discount.asBigDecimal()));
        if (discounted.asBigDecimal().signum() < 0)
            discounted = Money.zero();

        // duplicated logic - same tax calculation
        var tax = Money.of(discounted.asBigDecimal()
                .multiply(java.math.BigDecimal.valueOf(TAX_PERCENT))
                .divide(java.math.BigDecimal.valueOf(100)));
        var total = discounted.add(tax);

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
}