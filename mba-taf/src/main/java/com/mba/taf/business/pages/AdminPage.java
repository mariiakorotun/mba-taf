package com.mba.taf.business.pages;

import com.mba.taf.business.components.NavBar;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.mba.taf.business.locators.AdminLocators.*;

public class AdminPage extends BasePage {

    public AdminPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected By pageMarker() {
        return USER_ROWS;
    }

    @Step("Відкрити адмін-панель")
    public AdminPage open() {
        openPath("/admin");
        return waitUntilLoaded();
    }

    public AdminPage waitUntilLoaded() {
        waits.urlContains("/admin");
        waits.visible(PAGE_TITLE);
        return this;
    }

    public NavBar navBar() {
        return new NavBar(driver);
    }

    public int userCount() {
        return driver.findElements(USER_ROWS).size();
    }

    public boolean hasUser(String username) {
        return !driver.findElements(rowForUser(username)).isEmpty();
    }

    public String roleOf(String username) {
        return row(username).findElement(ROW_ROLE).getText().trim();
    }

    public boolean isActive(String username) {
        return row(username).findElement(ROW_STATUS).getText().contains("Активний");
    }

    private WebElement row(String username) {
        return waits.visible(rowForUser(username));
    }
}
