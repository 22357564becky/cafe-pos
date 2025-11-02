package com.cafepos;

import com.cafepos.printing.LegacyPrinterAdapter;
import com.cafepos.printing.Printer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FakeLegacy extends vendor.legacy.LegacyThermalPrinter {
    int lastLen = -1;

    @Override
    public void legacyPrint(byte[] payload) {
        lastLen = payload.length;
    }
}

class LegacyPrinterAdapterTest {

    @Test
    void text_to_bytes() {
        FakeLegacy fake = new FakeLegacy();
        Printer adapter = new LegacyPrinterAdapter(fake);

        adapter.print("ABC");

        assertTrue(fake.lastLen >= 3, "Expected at least 3 bytes from text 'ABC'");
    }
}
