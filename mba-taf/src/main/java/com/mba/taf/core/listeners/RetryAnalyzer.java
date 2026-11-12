package com.mba.taf.core.listeners;

import com.mba.taf.core.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger LOG = LoggerFactory.getLogger(RetryAnalyzer.class);
    private final int maxRetries = ConfigManager.getInt("retry.count", 0);
    private int attempt = 0;

    @Override
    public boolean retry(ITestResult result) {
        Throwable cause = result.getThrowable();
        if (cause instanceof AssertionError) {
            return false;
        }
        if (attempt < maxRetries) {
            attempt++;
            LOG.warn("Повтор {}/{} для {} через інфраструктурну помилку: {}",
                    attempt, maxRetries, result.getMethod().getMethodName(),
                    cause != null ? cause.getClass().getSimpleName() : "?");
            return true;
        }
        return false;
    }
}
