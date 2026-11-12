package com.mba.taf.business.locators;

import org.openqa.selenium.By;

public final class DashboardLocators {
    public static final By GREETING_USERNAME = By.cssSelector("main h2.h4 span");
    public static final By OWN_ACCOUNT_CARDS = By.cssSelector("main .card.shadow-lg");
    public static final By ADMIN_MONITORING_CARDS = By.cssSelector("main .card-monitor");
    public static final By ADMIN_CREATE_ACCOUNT_FORM = By.cssSelector("form[action$='/admin/accounts/create']");

    private DashboardLocators() {
    }
}
