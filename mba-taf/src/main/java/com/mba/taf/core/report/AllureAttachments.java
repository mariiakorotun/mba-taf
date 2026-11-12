package com.mba.taf.core.report;

import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public final class AllureAttachments {

    private static final Logger LOG = LoggerFactory.getLogger(AllureAttachments.class);

    private AllureAttachments() {
    }
    public static void attachUiFailureEvidence(WebDriver driver) {
        try {
            byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment("Скриншот при падінні", "image/png", new ByteArrayInputStream(png), "png");
            Allure.addAttachment("URL при падінні", "text/plain", driver.getCurrentUrl());
            Allure.addAttachment("HTML сторінки", "text/html",
                    new ByteArrayInputStream(driver.getPageSource().getBytes(StandardCharsets.UTF_8)), "html");
            LOG.info("Скриншот і HTML сторінки додано до звіту (URL: {})", driver.getCurrentUrl());
        } catch (RuntimeException e) {
            LOG.warn("Не вдалося зібрати діагностику UI: {}", e.getMessage());
        }
    }

    public static void text(String name, String content) {
        Allure.addAttachment(name, "text/plain", content);
    }
}
