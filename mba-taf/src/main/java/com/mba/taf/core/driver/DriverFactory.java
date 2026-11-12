package com.mba.taf.core.driver;

import com.mba.taf.core.config.ConfigManager;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;

public final class DriverFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DriverFactory.class);

    private DriverFactory() {
    }

    public static WebDriver create() {
        String browser = ConfigManager.get("browser", "chrome").toLowerCase();
        boolean headless = ConfigManager.getBool("headless", true);
        String remoteUrl = ConfigManager.get("remote.url", "");

        Capabilities options = switch (browser) {
            case "firefox" -> firefoxOptions(headless);
            case "edge" -> edgeOptions(headless);
            default -> chromeOptions(headless);
        };

        WebDriver driver;
        if (!remoteUrl.isBlank()) {
            driver = remote(remoteUrl, options);
        } else {
            driver = switch (browser) {
                case "firefox" -> new FirefoxDriver((FirefoxOptions) options);
                case "edge" -> new EdgeDriver((EdgeOptions) options);
                default -> new ChromeDriver((ChromeOptions) options);
            };
        }

        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ConfigManager.getInt("timeout.page.load", 30)));
        LOG.info("Запущено браузер {} (headless={}, remote={})", browser, headless, !remoteUrl.isBlank());
        return driver;
    }

    private static ChromeOptions chromeOptions(boolean headless) {
        ChromeOptions o = new ChromeOptions();
        if (headless) {
            o.addArguments("--headless=new");
        }
        o.addArguments("--window-size=1920,1080", "--no-sandbox", "--disable-dev-shm-usage");
        return o;
    }

    private static FirefoxOptions firefoxOptions(boolean headless) {
        FirefoxOptions o = new FirefoxOptions();
        if (headless) {
            o.addArguments("-headless");
        }
        o.addArguments("--width=1920", "--height=1080");
        return o;
    }

    private static EdgeOptions edgeOptions(boolean headless) {
        EdgeOptions o = new EdgeOptions();
        if (headless) {
            o.addArguments("--headless=new");
        }
        o.addArguments("--window-size=1920,1080");
        return o;
    }

    private static WebDriver remote(String url, Capabilities options) {
        try {
            return new RemoteWebDriver(URI.create(url).toURL(), options);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Некоректний remote.url: " + url, e);
        }
    }
}
