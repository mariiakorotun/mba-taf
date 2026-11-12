package com.mba.taf.core.listeners;

import com.mba.taf.core.driver.DriverManager;
import com.mba.taf.core.report.AllureAttachments;
import com.mba.taf.core.report.AllureEnvironmentWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.Arrays;
import java.util.stream.Collectors;

public class TestListener implements ITestListener, ISuiteListener, IInvokedMethodListener {

    private static final Logger LOG = LoggerFactory.getLogger(TestListener.class);

    @Override
    public void onStart(ISuite suite) {
        AllureEnvironmentWriter.write();
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult result) {
        if (method.isTestMethod() && result.getThrowable() != null && DriverManager.hasDriver()) {
            AllureAttachments.attachUiFailureEvidence(DriverManager.get());
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        MDC.put("test", name(result));
        LOG.info("▶ START  {}", name(result));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LOG.info("✔ PASSED {} ({} ms)", name(result), duration(result));
        MDC.remove("test");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        Throwable t = result.getThrowable();
        LOG.error("✘ FAILED {} ({} ms): {}", name(result), duration(result),
                t != null ? t.getClass().getSimpleName() + ": " + t.getMessage() : "невідома причина");
        MDC.remove("test");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        Throwable t = result.getThrowable();
        LOG.warn("↷ SKIPPED {}{}", name(result), t != null ? " — " + t.getMessage() : "");
        MDC.remove("test");
    }

    private static String name(ITestResult result) {
        String base = result.getTestClass().getRealClass().getSimpleName() + "." + result.getMethod().getMethodName();
        Object[] params = result.getParameters();
        if (params == null || params.length == 0) {
            return base;
        }
        return base + Arrays.stream(params).map(String::valueOf).collect(Collectors.joining(", ", "[", "]"));
    }

    private static long duration(ITestResult result) {
        return result.getEndMillis() - result.getStartMillis();
    }
}
