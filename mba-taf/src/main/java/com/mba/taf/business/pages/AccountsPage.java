package com.mba.taf.business.pages;

import com.mba.taf.business.components.NavBar;
import com.mba.taf.core.util.MoneyParser;
import com.mba.taf.data.model.OperationType;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.math.BigDecimal;

import static com.mba.taf.business.locators.AccountsLocators.*;

public class AccountsPage extends BasePage {

    public AccountsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected By pageMarker() {
        return PAGE_TITLE;
    }

    @Step("Відкрити сторінку «Мої рахунки»")
    public AccountsPage open() {
        openPath("/accounts");
        return waitUntilLoaded();
    }

    public AccountsPage waitUntilLoaded() {
        waits.urlContains("/accounts");
        waits.visible(PAGE_TITLE);
        return this;
    }

    public NavBar navBar() {
        return new NavBar(driver);
    }

    public boolean hasAccount(long accountId) {
        return !driver.findElements(cardById(accountId)).isEmpty();
    }

    public int accountCount() {
        return driver.findElements(ACCOUNT_CARDS).size();
    }

    public BigDecimal balanceOf(long accountId) {
        return MoneyParser.parse(card(accountId).findElement(CARD_BALANCE).getText());
    }

    public String currencyOf(long accountId) {
        return card(accountId).findElement(CARD_CURRENCY).getText().trim();
    }

    @Step("UI: поповнити рахунок #{accountId} на {amount}")
    public AccountsPage deposit(long accountId, String amount) {
        return submitOperation(accountId, CARD_DEPOSIT_AMOUNT, CARD_DEPOSIT_SUBMIT, amount);
    }

    @Step("UI: зняти з рахунку #{accountId} суму {amount}")
    public AccountsPage withdraw(long accountId, String amount) {
        return submitOperation(accountId, CARD_WITHDRAW_AMOUNT, CARD_WITHDRAW_SUBMIT, amount);
    }

    @Step("UI: чи прийме браузер суму «{amount}» для операції {operation} на рахунку #{accountId}")
    public boolean isAmountAcceptedByBrowser(long accountId, OperationType operation, String amount) {
        By input = operation == OperationType.DEPOSIT ? CARD_DEPOSIT_AMOUNT : CARD_WITHDRAW_AMOUNT;
        WebElement field = card(accountId).findElement(input);
        field.clear();
        if (!amount.isEmpty()) {
            field.sendKeys(amount);
        }
        Object valid = ((JavascriptExecutor) driver).executeScript("return arguments[0].checkValidity();", field);
        return Boolean.TRUE.equals(valid);
    }

    public String successMessage() {
        return isDisplayed(SUCCESS_ALERT) ? text(SUCCESS_ALERT) : "";
    }

    public String errorMessage() {
        return isDisplayed(ERROR_ALERT) ? text(ERROR_ALERT) : "";
    }

    private WebElement card(long accountId) {
        return waits.visible(cardById(accountId));
    }

    private AccountsPage submitOperation(long accountId, By amountInput, By submitButton, String amount) {
        WebElement card = card(accountId);
        WebElement field = card.findElement(amountInput);
        field.clear();
        field.sendKeys(amount);
        waits.clickable(card.findElement(submitButton)).click();
        waits.staleness(card);
        return waitUntilLoaded();
    }
}
