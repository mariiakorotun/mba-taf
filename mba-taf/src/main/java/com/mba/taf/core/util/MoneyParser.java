package com.mba.taf.core.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class MoneyParser {

    private MoneyParser() {
    }

    public static BigDecimal parse(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Сума відсутня");
        }
        String normalized = text
                .replaceAll("[^0-9,.\\-+]", "")   // прибрати валюту, пробіли, NBSP
                .replace("+", "")
                .replace(',', '.');
        return new BigDecimal(normalized).setScale(2, RoundingMode.UNNECESSARY);
    }
}
