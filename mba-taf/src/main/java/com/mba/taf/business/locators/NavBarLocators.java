package com.mba.taf.business.locators;

import org.openqa.selenium.By;

public final class NavBarLocators {
    public static final By ROOT = By.id("navbarNav");
    public static final By DASHBOARD_LINK = By.cssSelector("#navbarNav a[href$='/dashboard']");
    public static final By ACCOUNTS_LINK = By.cssSelector("#navbarNav a[href$='/accounts']");
    public static final By TRANSACTIONS_LINK = By.cssSelector("#navbarNav a[href$='/transactions']");
    public static final By ADMIN_LINK = By.cssSelector("#navbarNav a[href$='/admin']");
    public static final By LOGOUT_BUTTON = By.cssSelector("form[action$='/logout'] button[type='submit']");

    private NavBarLocators() {
    }
}
