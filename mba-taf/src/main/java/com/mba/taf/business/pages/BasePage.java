package com.mba.taf.business.pages;

import com.mba.taf.core.config.ConfigManager;
import com.mba.taf.core.wait.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
public abstract class BasePage {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final WebDriver driver;
    protected final Waits waits;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.waits = new Waits(driver);
    }

    protected abstract By pageMarker();

    public boolean isOpened() {
        return isDisplayed(pageMarker());
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }

    protected void openPath(String path) {
        String url = ConfigManager.get("base.url") + path;
        log.debug("Відкриття {}", url);
        driver.get(url);
    }

    protected void click(By locator) {
        waits.clickable(locator).click();
    }

    protected void type(By locator, String text) {
        WebElement element = waits.visible(locator);
        element.clear();
        if (text != null && !text.isEmpty()) {
            element.sendKeys(text);
        }
    }

    protected String text(By locator) {
        return waits.visible(locator).getText().trim();
    }

    protected boolean isDisplayed(By locator) {
        List<WebElement> found = driver.findElements(locator);
        return !found.isEmpty() && found.get(0).isDisplayed();
    }
}
