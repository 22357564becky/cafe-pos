package com.cafepos;

import java.math.BigDecimal;
import java.math.RoundingMode;
public final class Money implements Comparable<Money> {
    private final BigDecimal amount;
    public static Money of(double value) {
        if (value < 0) throw new IllegalArgumentException("Negative amounts not allowed");
        return new Money(BigDecimal.valueOf(value));
    }
    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }
    private Money(BigDecimal a) {
        if (a == null) throw new IllegalArgumentException("Amount required");
        this.amount = a.setScale(2, RoundingMode.HALF_UP);
    }
    public Money add(Money other) {
        return new Money(this.amount.add(other.amount));
    }
    public Money multiply(int qty) {
        if (qty < 0) throw new IllegalArgumentException("Cannot multiply by negative quantity");
        return new Money(this.amount.multiply(BigDecimal.valueOf(qty)));
    }

    @Override
    public int compareTo(Money other) {
        return this.amount.compareTo(other.amount);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Money other = (Money) obj;
        return amount.equals(other.amount);
    }

    @Override
    public int hashCode() {
        return amount.hashCode();
    }

    @Override
    public String toString(){
        return amount.toString();
    }


}