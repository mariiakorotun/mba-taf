package com.mba.taf.data.model;

import java.math.BigDecimal;

public record AmountCase(String id,
                         DataCategory category,
                         String initialBalance,
                         String amount,
                         ExpectedOutcome expected,
                         String expectedBalance,
                         String expectedMessage,
                         String description) {

    public BigDecimal initialBalanceValue() {
        return new BigDecimal(initialBalance);
    }

    public BigDecimal expectedBalanceValue() {
        return new BigDecimal(expectedBalance);
    }

    @Override
    public String toString() {
        return id + " " + category + ": " + description;
    }
}

