## Evolution

We keep the application as a layered monolith for now to optimise developer velocity and reduce operational complexity. A single deployable process makes local development, refactoring and end-to-end testing straightforward — important when the product and domain models are still evolving. The codebase is organised into clear layers (catalog, pricing, payments, printing, observers) so we avoid entangling responsibilities while remaining simple to run.

Natural seams for future partitioning include: Payments (isolate card/wallet logic and PCI concerns), Notifications/Observers (kitchen, delivery, customer channels), Catalog & Pricing (read-heavy services that can scale independently), and Printing/Adapters (hardware integrations). When splitting, prefer explicit contracts: REST (JSON) or gRPC for request/response APIs, and an event bus (lightweight message broker or cloud events) for notifications and async workflows. Use idempotent endpoints and versioned schemas for evolution; prefer async events to decouple availability and allow independent scaling. This keeps the system simple today but makes future extraction low-risk and incremental.

I would expose the factory pattern approach to application developers because it provides a simple, consistent interface
for creating complex objects without exposing construction details. This approach encapsulates the complexity of drink
customization behind intuitive product codes, making the API easy to use and understand. Developers can create
sophisticated drink combinations like "ESP+SHOT+OAT" with a single method call, without needing to know about the
underlying object relationships. This maintains clean separation of concerns while keeping the learning curve minimal
for new developers working with the codebase.

The smells that we removed from `OrderManagerGod.java` were:

- global state smell - used global variables - hard to test.
- long method smell - one big method does it all - less organised
- primitive obsession: `discountCode` strings; `TAX_PERCENT` as primitive; magic
  numbers for rates.
- duplicated Logic: `Money` and `BigDecimal` manipulations scattered inline.

The refactors we applied were:

- refactor(discount): Extract Class `DiscountPolicy` (behavior preserved)
- refactor(tax): Extract Class `FixedRateTaxPolic`y (behavior preserved)
- refactor(io): Extract `ReceiptPrinte`r (behavior preserved)
- refactor(payment): Replace Conditional with Polymorphism (`PaymentStrategy`)
- refactor(di): Constructor Injection; remove global `TAX_PERCENT/LAST_DISCOUNT_CODE`

Our new design satisfy the following SOLID principles:

- **Open/Closed Principle (OCP):**

The `DiscountPolicy` interface allows adding new discount types without changing OrderManagerGod.
You can just create a new class that implements `DiscountPolicy`.

- **Single Responsibility Principle (SRP):**

Each class now has a single, clear purpose.`FixedRateTaxPolicy`, `DiscountPolicy` handle tax and discount logic
separately.
The receipt printing, payment handling, and product creation are also delegated to specialized
classes (`ReceiptPrinter`,`PaymentStrategy`, `ProductFactory`).

- **Liskov Substitution Principle (LSP):**

All discount implementations (`LoyaltyPercentDiscount`, `FixedCouponDiscount`, `NoDiscount`) can replace each other via
the
`DiscountPolicy` interface without breaking functionality.

- **Interface Segregation Principle (ISP):**

Clients depend only on interfaces they need (`DiscountPolicy`, `PaymentStrategy`), rather than large classes that do
everything.

- **Dependency Inversion Principle (DIP):**

Instead of hard coding dependencies, the `OrderManagerGod` now uses constructor injection for `taxPercent`, and could
easily
be extended to inject services like `PricingService`.

To create a new discount type, we would create a new class (e.g `StudentDiscount`) that implements `DiscountPolicy` and
override the `discountOf` method to set the discount amount.
