package com.cafepos.printing;

import java.nio.charset.StandardCharsets;

public final class LegacyPrinterAdapter implements Printer {
    private final LegacyThermalPrinter adaptee;

    public LegacyPrinterAdapter(LegacyThermalPrinter adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void print(String reciptText) {
        byte[] escpos = reciptText.getBytes(StandardCharsets.UTF_8);
        adaptee.legacyPrint(escpos);
    }
}
