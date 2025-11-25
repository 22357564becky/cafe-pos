
package com.cafepos.app.events;

public sealed interface OrderEvents permits OrderCreated, OrderPaid {
}

