package com.mba.taf.core.wait;

import com.mba.taf.core.config.ConfigManager;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class Waits {

    private final FluentWait<WebDriver> wait;

    public Waits(WebDriver driver) {
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigManager.getInt("timeout.explicit", 10)))
                .pollingEvery(Duration.ofMillis(250))
                .ignoring(StaleElementReferenceException.class);
    }

    public WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public List<WebElement> allVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    public WebElement clickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public WebElement clickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    public void urlContains(String fragment) {
        wait.until(ExpectedConditions.urlContains(fragment));
    }

    public void staleness(WebElement element) {
        wait.until(driver -> {
            try {
                element.isEnabled();
                return false;
            } catch (StaleElementReferenceException | NoSuchElementException e) {
                return true;
            } catch (WebDriverException e) {
                if (e.getMessage() != null && e.getMessage().contains("does not belong to the document")) {
                    return true;
                }
                throw e;
            }
        });
    }

    public void presenceOf(By locator) {
        wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }
}
