package com.mba.taf.core.report;

import com.mba.taf.core.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class AllureEnvironmentWriter {

    private static final Logger LOG = LoggerFactory.getLogger(AllureEnvironmentWriter.class);

    private AllureEnvironmentWriter() {
    }

    public static void write() {
        Path dir = Path.of(System.getProperty("allure.results.directory", "target/allure-results"));
        try {
            Files.createDirectories(dir);
            String env = String.join("\n",
                    "Environment=" + ConfigManager.env(),
                    "Base.URL=" + ConfigManager.get("base.url"),
                    "Browser=" + ConfigManager.get("browser", "chrome"),
                    "Headless=" + ConfigManager.get("headless", "true"),
                    "Java=" + System.getProperty("java.version"),
                    "OS=" + System.getProperty("os.name"));
            Files.writeString(dir.resolve("environment.properties"), env, StandardCharsets.UTF_8);

            try (InputStream categories = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("allure/categories.json")) {
                if (categories != null) {
                    Files.copy(categories, dir.resolve("categories.json"), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (IOException e) {
            LOG.warn("Не вдалося записати environment/categories для Allure: {}", e.getMessage());
        }
    }
}
