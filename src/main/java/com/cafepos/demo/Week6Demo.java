package com.cafepos.demo;

import com.cafepos.factory.*;
import com.cafepos.pricing.*;
import com.cafepos.common.Money;
import com.cafepos.smells.*;
import com.cafepos.catalog.*;
import com.cafepos.domain.*;
import com.cafepos.payment.*;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class Week6Demo {
    public static void main(String[] args) {
        // Old (smelly) flow
        String oldReceipt = OrderManagerGod.process("LAT+L", 2, "CARD", "LOYAL5", false);
        // New (clean) flow using PricingService + ReceiptPrinter
        var pricing = new PricingService(new LoyaltyPercentDiscount(5), new FixedRateTaxPolicy(10));
        var printer = new ReceiptPrinter();
        var checkout = new CheckoutService(new ProductFactory(), pricing, printer, 10);
        String newReceipt = checkout.checkout("LAT+L", 2);

        System.out.println("Old Receipt: \n" + oldReceipt);
        System.out.println("\nNew Receipt: \n" + newReceipt);
        System.out.println("\nMatch: " + oldReceipt.equals(newReceipt));

        // CLI demo
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- CLI Order Entry ---\n");
        System.out.println(
                "Menu Codes: \nESP - Espresso\nLAT - Latte\nCAP - Cappuccino\nSHOT - Extra Shot\nOAT - Oat Milk\nSYP - Syrup\nL - Large Size\n");

        Order cliOrder = new Order(OrderIds.next());
        List<String> enteredRecipes = new ArrayList<>();

        while (true) {
            System.out.print("Product code (or 'done' to finish): ");
            String code = scanner.nextLine().trim();
            if (code.equalsIgnoreCase("done"))
                break;
            Product product = new ProductFactory().create(code);
            if (product == null) {
                System.out.println("Unknown product code. Try again.");
                continue;
            }
            System.out.print("Quantity: ");
            int qty = 1;
            try {
                qty = Integer.parseInt(scanner.nextLine().trim());
                if (qty <= 0) {
                    System.out.println("Quantity must be positive. Defaulting to 1.");
                    qty = 1;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid quantity. Defaulting to 1.");
                qty = 1;
            }
            cliOrder.addItem(new LineItem(product, qty));
            enteredRecipes.add(code);
            System.out.println("Added: " + product.name() + " x " + qty);
        }

        System.out.println("\nCafe Order Summary: ");
        System.out.println("Order #" + cliOrder.id());
        for (LineItem li : cliOrder.items()) {
            System.out.println(" - " + li.product().name() + " x " + li.quantity() + " = $" + li.lineTotal());
        }
        System.out.println("Subtotal: $" + cliOrder.subtotal());
        System.out.println("Tax (10%): $" + cliOrder.taxAtPercent(10));
        System.out.println("Total: $" + cliOrder.totalWithTax(10));

        System.out.print("Discount code (LOYAL5, COUPON1 or press Enter for none): ");
        String discountCli = scanner.nextLine().trim();
        if (discountCli.isEmpty())
            discountCli = null;

        DiscountPolicy orderPolicy;
        if ("LOYAL5".equalsIgnoreCase(discountCli)) {
            orderPolicy = new LoyaltyPercentDiscount(5);
        } else if ("COUPON1".equalsIgnoreCase(discountCli)) {
            orderPolicy = new FixedCouponDiscount(Money.of(1.00));
        } else {
            orderPolicy = new NoDiscount();
        }
        PricingService orderPricing = new PricingService(orderPolicy, new FixedRateTaxPolicy(10));
        PricingService.PricingResult orderPr = orderPricing.price(cliOrder.subtotal());
        ReceiptPrinter orderPrinter = new ReceiptPrinter();
        int totalQty = cliOrder.items().stream().mapToInt(LineItem::quantity).sum();
        String newFullReceipt = orderPrinter.format("ORDER", totalQty, orderPr, 10);
        System.out.println("\n--- New receipt for full order ---\n" + newFullReceipt);

        PaymentStrategy strategy = choosePaymentStrategy(scanner, cliOrder);
        if (strategy != null) {
            cliOrder.pay(strategy);
        } else {
            System.out.println("No valid payment selected. Order not paid.");
        }

        // compare old vs new for the first item
        if (!enteredRecipes.isEmpty()) {
            String firstRecipe = enteredRecipes.get(0);
            LineItem firstItem = cliOrder.items().get(0);

            String oldCli = OrderManagerGod.process(firstRecipe, firstItem.quantity(), null, discountCli, false);

            DiscountPolicy matchPolicy;
            String matchDiscountCode = discountCli;
            if ("LOYAL5".equalsIgnoreCase(matchDiscountCode)) {
                matchPolicy = new LoyaltyPercentDiscount(5);
            } else if ("COUPON1".equalsIgnoreCase(matchDiscountCode)) {
                matchPolicy = new FixedCouponDiscount(Money.of(1.00));
            } else {
                matchPolicy = new NoDiscount();
            }
            PricingService matchPricing = new PricingService(matchPolicy, new FixedRateTaxPolicy(10));
            CheckoutService matchCheckout = new CheckoutService(new ProductFactory(), matchPricing,
                    new ReceiptPrinter(), 10);
            String newCli = matchCheckout.checkout(firstRecipe, firstItem.quantity());
            System.out.println("\n--- Match for first item ---");
            System.out.println("\nOld Receipt:\n" + oldCli);
            System.out.println("\nNew Receipt:\n" + newCli);
            System.out.println("Match: " + oldCli.equals(newCli));
        }

        scanner.close();

    }

    private static PaymentStrategy choosePaymentStrategy(Scanner scanner, Order order) {
        System.out.println("\nSelect payment method: (cash / card / wallet)");
        System.out.print("Method: ");
        String method = scanner.nextLine().trim().toLowerCase();
        switch (method) {
            case "cash":
                return new CashPayment();
            case "card":
                System.out.print("Enter card number (min 4 digits): ");
                String card = scanner.nextLine().trim();
                try {
                    return new CardPayment(card);
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid card: " + e.getMessage());
                    return null;
                }
            case "wallet":
                System.out.print("Enter wallet id (your name): ");
                String wallet = scanner.nextLine().trim();
                try {
                    return new WalletPayment(wallet);
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid wallet id: " + e.getMessage());
                    return null;
                }
            default:
                System.out.println("Unknown payment method: " + method);
                return null;
        }
    }

}
