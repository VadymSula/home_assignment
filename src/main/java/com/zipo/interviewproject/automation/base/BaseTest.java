package com.zipo.interviewproject.automation.base;

import com.zipo.interviewproject.automation.config.FrameworkConfig;
import com.zipo.interviewproject.automation.device.DeviceAction;
import com.zipo.interviewproject.automation.diagnostics.FailureDiagnostics;
import com.zipo.interviewproject.automation.driver.DriverFactory;
import com.zipo.interviewproject.automation.listeners.TestExecutionListener;
import io.appium.java_client.android.AndroidDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

import java.lang.reflect.Method;

@Listeners(TestExecutionListener.class)
public abstract class BaseTest {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(BaseTest.class);

    protected final FrameworkConfig config = FrameworkConfig.load();

    private DeviceAction device;

    @BeforeMethod(alwaysRun = true)
    protected final void startDriverSession(
            Method method, ITestResult result) {

        LOGGER.info("Starting test: {}.{}",
                getClass().getSimpleName(), method.getName());

        String deviceSerial =
                FailureDiagnostics.begin(config, result);

        device = new DeviceAction(
                DriverFactory.createDriver(config, deviceSerial),
                config.explicitWaitTimeout());
    }

    @AfterMethod(alwaysRun = true)
    protected final void stopDriverSession(ITestResult result)
            throws Exception {

        try (AutoCloseable session = DriverFactory::quitDriver) {
            if (device != null) {
                beforeDriverTermination(result);
            }

        } catch (Exception | AssertionError cleanupFailure) {
            Throwable testFailure = result.getThrowable();

            LOGGER.warn("Session cleanup failed", cleanupFailure);

            if (isFailed(result) && testFailure != null) {
                if (testFailure != cleanupFailure) {
                    testFailure.addSuppressed(cleanupFailure);
                }
            } else {
                throw cleanupFailure;
            }

        } finally {
            device = null;

            FailureDiagnostics.release(result);

            LOGGER.info("Test method result: {}.{} — {}",
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

    private static boolean isFailed(ITestResult result) {
        return result.getStatus() == ITestResult.FAILURE
                || result.getStatus()
                == ITestResult.SUCCESS_PERCENTAGE_FAILURE;
    }

    private static String getTestStatus(ITestResult result) {
        return switch (result.getStatus()) {
            case ITestResult.SUCCESS -> "PASSED";
            case ITestResult.FAILURE -> "FAILED";
            case ITestResult.SKIP -> "SKIPPED";
            case ITestResult.SUCCESS_PERCENTAGE_FAILURE ->
                    "FAILED_WITHIN_SUCCESS_PERCENTAGE";
            default -> "UNKNOWN";
        };
    }
}