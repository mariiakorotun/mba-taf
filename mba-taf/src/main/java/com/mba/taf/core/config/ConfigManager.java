package com.mba.taf.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public final class ConfigManager {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigManager.class);
    private static final String ENV = System.getProperty("env", "dev").trim();
    private static final Properties PROPS = load(ENV);

    private ConfigManager() {
    }

    private static Properties load(String env) {
        String path = "config/" + env + ".properties";
        Properties props = new Properties();
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalStateException("Не знайдено файл конфігурації середовища: " + path);
            }
            props.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        LOG.info("Завантажено конфігурацію середовища '{}' ({})", env, path);
        return props;
    }

    public static String env() {
        return ENV;
    }

    public static String get(String key) {
        String value = resolve(key);
        if (value == null) {
            throw new IllegalStateException("Відсутній параметр конфігурації '" + key + "'. Задайте його у config/"
                    + ENV + ".properties, через -D" + key + " або змінну оточення " + envVarName(key));
        }
        return value;
    }

    public static String get(String key, String defaultValue) {
        String value = resolve(key);
        return value != null ? value : defaultValue;
    }

    public static int getInt(String key, int defaultValue) {
        return Integer.parseInt(get(key, String.valueOf(defaultValue)));
    }

    public static boolean getBool(String key, boolean defaultValue) {
        return Boolean.parseBoolean(get(key, String.valueOf(defaultValue)));
    }

    private static String resolve(String key) {
        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank()) {
            return sys.trim();
        }
        String envVar = System.getenv(envVarName(key));
        if (envVar != null && !envVar.isBlank()) {
            return envVar.trim();
        }
        String fromFile = PROPS.getProperty(key);
        return (fromFile == null || fromFile.isBlank()) ? null : fromFile.trim();
    }

    private static String envVarName(String key) {
        return "MBA_" + key.toUpperCase().replace('.', '_');
    }
}
