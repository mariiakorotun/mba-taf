package com.mba.taf.business.locators;

import org.openqa.selenium.By;

public final class HomeLocators {
    public static final By HERO_TITLE = By.cssSelector(".hero-section h1");
    public static final By NAV_LOGIN_BUTTON = By.cssSelector("nav a[href$='/login']");
    public static final By FEATURE_CARDS = By.cssSelector("#features .card");

    private HomeLocators() {
    }
}
