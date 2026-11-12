package com.mba.taf.business.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.mba.taf.business.locators.LoginLocators.*;

public class LoginPage extends BasePage {

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected By pageMarker() {
        return USERNAME;
    }

    @Step("Відкрити сторінку входу")
    public LoginPage open() {
        openPath("/login");
        return waitUntilLoaded();
    }

    public LoginPage waitUntilLoaded() {
        waits.visible(USERNAME);
        return this;
    }

    @Step("Увійти як «{username}» (очікується успіх)")
    public DashboardPage loginAs(String username, String password) {
        submitCredentials(username, password);
        return new DashboardPage(driver).waitUntilLoaded();
    }

    @Step("Увійти як «{username}» (очікується відмова сервера)")
    public LoginPage loginExpectingError(String username, String password) {
        submitCredentials(username, password);
        waits.urlContains("/login?error");
        return waitUntilLoaded();
    }

    @Step("Спроба входу з порожнім полем «{username}» — перевірка HTML5-валідації")
    public boolean isSubmitBlockedByBrowser(String username, String password) {
        type(USERNAME, username);
        type(PASSWORD, password);
        WebElement form = driver.findElement(By.cssSelector("form[action$='/login']"));
        Object valid = ((JavascriptExecutor) driver).executeScript("return arguments[0].checkValidity();", form);
        return Boolean.FALSE.equals(valid);
    }

    public boolean isErrorShown() {
        return isDisplayed(ERROR_ALERT);
    }

    public String errorMessage() {
        return text(ERROR_ALERT);
    }

    public boolean isLogoutMessageShown() {
        return isDisplayed(LOGOUT_ALERT);
    }

    private void submitCredentials(String username, String password) {
        type(USERNAME, username);
        type(PASSWORD, password);
        click(SUBMIT);
    }
}
