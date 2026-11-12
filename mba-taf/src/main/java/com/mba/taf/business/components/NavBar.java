package com.mba.taf.business.components;

import com.mba.taf.business.pages.AccountsPage;
import com.mba.taf.business.pages.AdminPage;
import com.mba.taf.business.pages.DashboardPage;
import com.mba.taf.business.pages.LoginPage;
import com.mba.taf.business.pages.TransactionsPage;
import com.mba.taf.core.wait.Waits;
import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;

import static com.mba.taf.business.locators.NavBarLocators.*;

public class NavBar {

    private final WebDriver driver;
    private final Waits waits;

    public NavBar(WebDriver driver) {
        this.driver = driver;
        this.waits = new Waits(driver);
    }

    @Step("Перейти до «Рахунки»")
    public AccountsPage goToAccounts() {
        waits.clickable(ACCOUNTS_LINK).click();
        return new AccountsPage(driver).waitUntilLoaded();
    }

    @Step("Перейти до «Транзакції»")
    public TransactionsPage goToTransactions() {
        waits.clickable(TRANSACTIONS_LINK).click();
        return new TransactionsPage(driver).waitUntilLoaded();
    }

    @Step("Перейти до «Головна»")
    public DashboardPage goToDashboard() {
        waits.clickable(DASHBOARD_LINK).click();
        return new DashboardPage(driver).waitUntilLoaded();
    }

    @Step("Перейти до «Адмін-панель»")
    public AdminPage goToAdmin() {
        waits.clickable(ADMIN_LINK).click();
        return new AdminPage(driver).waitUntilLoaded();
    }

    public boolean isAdminLinkVisible() {
        return !driver.findElements(ADMIN_LINK).isEmpty() && driver.findElement(ADMIN_LINK).isDisplayed();
    }

    @Step("Вийти з системи")
    public LoginPage logout() {
        waits.clickable(LOGOUT_BUTTON).click();
        waits.urlContains("/login?logout");
        return new LoginPage(driver).waitUntilLoaded();
    }
}
