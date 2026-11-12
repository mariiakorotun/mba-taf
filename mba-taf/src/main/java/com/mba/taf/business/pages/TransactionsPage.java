package com.mba.taf.business.pages;

import com.mba.taf.business.components.NavBar;
import com.mba.taf.business.locators.TransactionsLocators;
import com.mba.taf.core.util.MoneyParser;
import com.mba.taf.data.model.OperationType;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.math.BigDecimal;
import java.util.List;

import static com.mba.taf.business.locators.TransactionsLocators.*;

public class TransactionsPage extends BasePage {

    public record TransactionRow(OperationType type, String accountLabel, BigDecimal signedAmount, String currency) {
    }

    public TransactionsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected By pageMarker() {
        return TABLE;
    }

    @Step("Відкрити сторінку «Історія транзакцій»")
    public TransactionsPage open() {
        openPath("/transactions");
        return waitUntilLoaded();
    }

    public TransactionsPage waitUntilLoaded() {
        waits.urlContains("/transactions");
        waits.visible(TABLE);
        return this;
    }

    public NavBar navBar() {
        return new NavBar(driver);
    }

    @Step("Зчитати транзакції рахунку #{accountId}")
    public List<TransactionRow> rowsForAccount(long accountId) {
        return driver.findElements(TransactionsLocators.rowsForAccount(accountId)).stream().map(this::toRow).toList();
    }

    private TransactionRow toRow(WebElement row) {
        String typeText = row.findElement(ROW_TYPE).getText();
        OperationType type = typeText.contains("Поповнення") ? OperationType.DEPOSIT : OperationType.WITHDRAW;
        String amountText = row.findElement(ROW_AMOUNT).getText().trim();   // напр. «+250.00 UAH» / «-100.00 UAH»
        String[] parts = amountText.split("\\s+");
        String currency = parts.length > 1 ? parts[parts.length - 1] : "";
        return new TransactionRow(type, row.findElement(ROW_ACCOUNT).getText().trim(),
                MoneyParser.parse(parts[0]), currency);
    }
}
