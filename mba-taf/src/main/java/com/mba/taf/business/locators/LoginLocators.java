package com.mba.taf.business.locators;

import org.openqa.selenium.By;

public final class LoginLocators {
    public static final By USERNAME = By.name("username");
    public static final By PASSWORD = By.name("password");
    public static final By SUBMIT = By.cssSelector("form[action$='/login'] button[type='submit']");
    public static final By ERROR_ALERT = By.cssSelector(".alert-danger");
    public static final By LOGOUT_ALERT = By.cssSelector(".alert-success");

    private LoginLocators() {
    }
}
