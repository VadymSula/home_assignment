package com.zipo.interviewproject.automation.base;

import com.zipo.interviewproject.automation.config.FrameworkConfig;
import com.zipo.interviewproject.automation.device.DeviceAction;
import com.zipo.interviewproject.automation.driver.DriverFactory;
import io.appium.java_client.android.AndroidDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

public abstract class BaseTest {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(BaseTest.class);

    protected final FrameworkConfig config = FrameworkConfig.load();

    private DeviceAction device;

    @BeforeMethod(alwaysRun = true)
    protected final void startDriverSession(Method method) {
        LOGGER.info("Starting test: {}.{}",
                getClass().getSimpleName(), method.getName());

        device = new DeviceAction(
                DriverFactory.createDriver(config),
                config.explicitWaitTimeout());
    }

    @AfterMethod(alwaysRun = true)
    protected final void stopDriverSession(ITestResult result) throws Exception {
        try (AutoCloseable session = DriverFactory::quitDriver) {
            if (device != null) {
                beforeDriverTermination(result);
            }
        } catch (Exception | AssertionError cleanupFailure) {
            Throwable testFailure = result.getThrowable();

            LOGGER.warn("Session cleanup failed", cleanupFailure);

            if (result.getStatus() == ITestResult.FAILURE
                    && testFailure != null) {
                testFailure.addSuppressed(cleanupFailure);
            } else {
                throw cleanupFailure;
            }
        } finally {
            device = null;

            LOGGER.info("Test execution finished: {}.{} — {}",
                    getClass().getSimpleName(),
                    result.getMethod().getMethodName(),
                    getTestStatus(result));
        }
    }

    protected final AndroidDriver getDriver() {
        return DriverFactory.getDriver();
    }

    protected final DeviceAction device() {
        return device;
    }

    /**
     * Allows additional cleanup before the driver session is terminated.
     */
    protected void beforeDriverTermination(ITestResult result) {
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