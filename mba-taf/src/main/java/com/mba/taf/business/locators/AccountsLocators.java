package com.mba.taf.business.locators;

import org.openqa.selenium.By;

public final class AccountsLocators {
    public static final By PAGE_TITLE = By.cssSelector("main h2.fw-bold");
    public static final By ACCOUNT_CARDS = By.cssSelector(".account-card");
    public static final By SUCCESS_ALERT = By.cssSelector(".alert-success");
    public static final By ERROR_ALERT = By.cssSelector(".alert-danger");

    public static final By CARD_BALANCE = By.cssSelector(".balance-value span:first-child");
    public static final By CARD_CURRENCY = By.cssSelector(".balance-value span:last-child");
    public static final By CARD_DEPOSIT_AMOUNT = By.cssSelector("form[action$='/deposit'] input[name='amount']");
    public static final By CARD_DEPOSIT_SUBMIT = By.cssSelector("form[action$='/deposit'] button[type='submit']");
    public static final By CARD_WITHDRAW_AMOUNT = By.cssSelector("form[action$='/withdraw'] input[name='amount']");
    public static final By CARD_WITHDRAW_SUBMIT = By.cssSelector("form[action$='/withdraw'] button[type='submit']");

    private AccountsLocators() {}

    public static By cardById(long accountId) {
        return By.xpath("//div[contains(@class,'account-card')][.//span[normalize-space()='ID: " + accountId + "']]");
    }
}
