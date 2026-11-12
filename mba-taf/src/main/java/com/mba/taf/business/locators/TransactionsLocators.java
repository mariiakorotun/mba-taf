package com.mba.taf.business.locators;

import org.openqa.selenium.By;

public final class TransactionsLocators {
    public static final By PAGE_TITLE = By.cssSelector("main h2.fw-bold");
    public static final By TABLE = By.cssSelector("main table");

    public static final By ROW_TYPE = By.cssSelector("td:nth-child(2) span");
    public static final By ROW_ACCOUNT = By.cssSelector("td:nth-child(3) span");
    public static final By ROW_AMOUNT = By.cssSelector("td.amount-cell span");

    private TransactionsLocators() {
    }

    public static By rowsForAccount(long accountId) {
        return By.xpath("//tbody/tr[.//td[3]//span[starts-with(normalize-space(),'#" + accountId + " ')]]");
    }
}
