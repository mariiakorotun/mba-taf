package com.mba.taf.business.pages;

import com.mba.taf.business.components.NavBar;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static com.mba.taf.business.locators.DashboardLocators.*;

public class DashboardPage extends BasePage {

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected By pageMarker() {
        return GREETING_USERNAME;
    }

    @Step("Відкрити головну сторінку кабінету")
    public DashboardPage open() {
        openPath("/dashboard");
        return waitUntilLoaded();
    }

    public DashboardPage waitUntilLoaded() {
        waits.urlContains("/dashboard");
        waits.visible(GREETING_USERNAME);
        return this;
    }

    public NavBar navBar() {
        return new NavBar(driver);
    }

    public String greetingUsername() {
        return text(GREETING_USERNAME);
    }

    public int ownAccountCount() {
        return driver.findElements(OWN_ACCOUNT_CARDS).size();
    }

    public boolean isAdminToolsVisible() {
        return isDisplayed(ADMIN_CREATE_ACCOUNT_FORM);
    }
}
