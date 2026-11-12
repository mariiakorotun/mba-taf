package com.mba.taf.business.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static com.mba.taf.business.locators.HomeLocators.*;

public class HomePage extends BasePage {

    public HomePage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected By pageMarker() {
        return HERO_TITLE;
    }

    @Step("Відкрити публічну сторінку застосунку")
    public HomePage open() {
        openPath("/");
        return waitUntilLoaded();
    }

    public HomePage waitUntilLoaded() {
        waits.visible(HERO_TITLE);
        return this;
    }

    public String heroTitle() {
        return text(HERO_TITLE);
    }

    public int featureCount() {
        return driver.findElements(FEATURE_CARDS).size();
    }

    @Step("Натиснути «Вхід до банку»")
    public LoginPage goToLogin() {
        click(NAV_LOGIN_BUTTON);
        return new LoginPage(driver).waitUntilLoaded();
    }
}
