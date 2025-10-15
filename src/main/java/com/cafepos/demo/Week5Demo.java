package com.cafepos.demo;

import com.cafepos.catalog.Product;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import com.cafepos.domain.OrderIds;
import com.cafepos.factory.ProductFactory;
import com.cafepos.payment.PaymentStrategy;
import com.cafepos.payment.CashPayment;
import com.cafepos.payment.CardPayment;
import com.cafepos.payment.WalletPayment;
import java.util.Scanner;

public final class Week5Demo {
    public static void main(String[] args) {
        ProductFactory factory = new ProductFactory();

        Product p1 = factory.create("ESP+SHOT+OAT");
        Product p2 = factory.create("LAT+L");

        Order order = new Order(OrderIds.next());
        order.addItem(new LineItem(p1, 1));
        order.addItem(new LineItem(p2, 2));

        System.out.println("Order #" + order.id());

        for (LineItem li : order.items()) {
            System.out.println(" - " + li.product().name() + " x " + li.quantity() + " = $" + li.lineTotal());
        }

        System.out.println("Subtotal: $" + order.subtotal());
        System.out.println("Tax (10%): $" + order.taxAtPercent(10));
        System.out.println("Total: $" + order.totalWithTax(10));

        runCliDemo(factory);
    }

    private static void runCliDemo(ProductFactory factory) {
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        Order cliOrder = new Order(OrderIds.next());

        System.out.println("\n--- Cafe Order Entry ---\n");
        System.out.println("Menu Codes \nESP - Espresso\nLAT - Latte\nCAP - Cappuccino\nSHOT - Extra Shot\nOAT - Oat Milk\nSYP - Syrup\nL - Large Size\n");
        System.out.println("Enter product codes and quantities. Type 'done' to finish.");

        while (true) {
            System.out.print("Product code (or 'done'): ");
            String code = scanner.nextLine().trim();

            if (code.equalsIgnoreCase("done")) break;

            Product product = factory.create(code);
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
            }

            cliOrder.addItem(new LineItem(product, qty));
            System.out.println("Added: " + product.name() + " x " + qty);
        }

        System.out.println("\nCafe Order Summary");
        System.out.println("Order #" + cliOrder.id());

        for (LineItem li : cliOrder.items()) {
            System.out.println(" - " + li.product().name() + " x " + li.quantity() + " = $" + li.lineTotal());
        }

        System.out.println("Subtotal: $" + cliOrder.subtotal());
        System.out.println("Tax (10%): $" + cliOrder.taxAtPercent(10));
        System.out.println("Total: $" + cliOrder.totalWithTax(10));

        PaymentStrategy strategy = choosePaymentStrategy(scanner, cliOrder);
        if (strategy != null) {
            strategy.pay(cliOrder);
        } else {
            System.out.println("No valid payment selected. Order not paid.");
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