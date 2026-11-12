package com.mba.taf.business.locators;

import org.openqa.selenium.By;

public final class AdminLocators {
    public static final By PAGE_TITLE = By.cssSelector("main h2");
    public static final By USER_ROWS = By.cssSelector("main table tbody tr");
    public static final By ROW_ROLE = By.cssSelector("td:nth-child(2) span");
    public static final By ROW_STATUS = By.cssSelector("td:nth-child(3) span span");

    private AdminLocators() {
    }

    public static By rowForUser(String username) {
        return By.xpath("//tbody/tr[.//td[1]//span[normalize-space()='" + username + "']]");
    }
}
