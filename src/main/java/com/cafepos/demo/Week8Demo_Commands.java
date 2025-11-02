package com.cafepos.demo;

import com.cafepos.domain.*;
import com.cafepos.payment.*;
import com.cafepos.command.*;
import java.util.Scanner;

public final class Week8Demo_Commands {
    public static void main(String[] args) {
        // Run the original scripted demo
        runScriptedDemo();
        // Run the interactive CLI demo
        runCliDemo();
    }

    // Original scripted demo for reference
    private static void runScriptedDemo() {
        Order order = new Order(OrderIds.next());
        OrderService service = new OrderService(order);
        PosRemote remote = new PosRemote(3);
        remote.setSlot(0, new AddItemCommand(service, "ESP+SHOT+OAT", 1));
        remote.setSlot(1, new AddItemCommand(service, "LAT+L", 2));
        remote.setSlot(2, new PayOrderCommand(service, new CardPayment("1234567890123456"), 10));
        remote.press(0);
        remote.press(1);
        remote.undo(); // remove last add
        remote.press(1); // add again
        remote.press(2); // pay
    }

    private static void runCliDemo() {
        System.out.println("\n--- CafePos Café---\n");
        System.out.println("Menu Codes (with prices):");
        System.out.println("  ESP - Espresso           €2.50");
        System.out.println("  LAT - Latte              €3.20");
        System.out.println("  CAP - Cappuccino         €3.00");
        System.out.println("  SHOT - Extra Shot        +€0.80");
        System.out.println("  OAT - Oat Milk           +€0.50");
        System.out.println("  SYP - Syrup              +€0.40");
        System.out.println("  L   - Large Size         +€0.70");
        System.out.println("Commands: add, remove, pay, summary, quit");

        Order order = new Order(OrderIds.next());
        OrderService service = new OrderService(order);
        PosRemote remote = new PosRemote(3);

        Scanner scanner = new java.util.Scanner(System.in);
        boolean paid = false;
        while (true) {
            System.out.print("\nEnter command (add/remove/pay/summary/quit): ");
            String cmd = scanner.nextLine().trim().toLowerCase();
            switch (cmd) {
                case "add" -> {
                    System.out.print("Product code: ");
                    String code = scanner.nextLine().trim();
                    System.out.print("Quantity: ");
                    int qty = 1;
                    try {
                        qty = Integer.parseInt(scanner.nextLine().trim());
                        if (qty <= 0)
                            qty = 1;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid quantity. Defaulting to 1.");
                    }
                    remote.setSlot(0, new AddItemCommand(service, code, qty));
                    remote.press(0);
                }
                case "remove" -> {
                    remote.setSlot(1, () -> service.removeLastItem());
                    remote.press(1);
                }
                case "pay" -> {
                    if (order.items().isEmpty()) {
                        System.out.println("No items in order to pay for.");
                        break;
                    }
                    System.out.print("Payment method (cash/card/wallet): ");
                    String method = scanner.nextLine().trim().toLowerCase();
                    PaymentStrategy strategy = null;
                    if (method.equals("cash")) {
                        strategy = new CashPayment();
                    } else if (method.equals("card")) {
                        System.out.print("Enter card number: ");
                        String card = scanner.nextLine().trim();
                        try {
                            strategy = new CardPayment(card);
                        } catch (Exception e) {
                            System.out.println("Invalid card: " + e.getMessage());
                        }
                    } else if (method.equals("wallet")) {
                        System.out.print("Enter wallet id: ");
                        String wallet = scanner.nextLine().trim();
                        try {
                            strategy = new WalletPayment(wallet);
                        } catch (Exception e) {
                            System.out.println("Invalid wallet: " + e.getMessage());
                        }
                    }
                    if (strategy != null) {
                        remote.setSlot(2, new PayOrderCommand(service, strategy, 10));
                        remote.press(2);
                        paid = true;
                    } else {
                        System.out.println("No valid payment selected.");
                    }
                }
                case "summary" -> {
                    System.out.println("\nOrder #" + order.id());
                    for (LineItem li : order.items()) {
                        System.out
                                .println(" - " + li.product().name() + " x " + li.quantity() + " = $" + li.lineTotal());
                    }
                    System.out.println("Subtotal: $" + order.subtotal());
                    System.out.println("Tax (10%): $" + order.taxAtPercent(10));
                    System.out.println("Total: $" + order.totalWithTax(10));
                }
                case "quit" -> {
                    if (!paid && !order.items().isEmpty()) {
                        System.out.print("You have not paid yet. Pay now? (y/n): ");
                        String yn = scanner.nextLine().trim().toLowerCase();
                        if (yn.equals("y")) {
                            cmd = "pay";
                            continue;
                        }
                    }
                    System.out.println("Thank you for your Service!, please come again :)");
                    return;
                }
                default -> System.out.println("Unknown command. Try add, remove, pay, summary, or quit.");
            }
        }
    }
}