package com.zipo.interviewproject.automation.base;

import com.zipo.interviewproject.automation.config.FrameworkConfig;
import com.zipo.interviewproject.automation.driver.DriverFactory;
import io.appium.java_client.android.AndroidDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

public abstract class BaseTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseTest.class);
    protected final FrameworkConfig config = FrameworkConfig.load();

    @BeforeMethod(alwaysRun = true)
    protected final void startDriverSession(Method method) {
        LOGGER.info("Starting test: {}.{}",
                getClass().getSimpleName(), method.getName());

        DriverFactory.createDriver(config);
    }

    @AfterMethod(alwaysRun = true)
    protected final void stopDriverSession(ITestResult result) {
        try {
            DriverFactory.quitDriver();
        } finally {
            LOGGER.info("Test finished: {}.{} — {}",
                    getClass().getSimpleName(),
                    result.getMethod().getMethodName(),
                    getTestStatus(result));
        }
    }

    protected final AndroidDriver getDriver() {
        return DriverFactory.getDriver();
    }

    private static String getTestStatus(ITestResult result) {
        return switch (result.getStatus()) {
            case ITestResult.SUCCESS -> "PASSED";
            case ITestResult.FAILURE -> "FAILED";
            case ITestResult.SKIP -> "SKIPPED";
            default -> "UNKNOWN";
        };
    }
}