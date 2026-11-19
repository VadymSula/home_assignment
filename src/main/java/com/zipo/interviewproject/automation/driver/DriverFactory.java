package com.zipo.interviewproject.automation.driver;

import com.zipo.interviewproject.automation.config.FrameworkConfig;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;

/** Owns the Android driver session for each test execution thread. */
public final class DriverFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DriverFactory.class);
    private static final ThreadLocal<AndroidDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {
    }

    public static AndroidDriver createDriver(FrameworkConfig config) {
        if (DRIVER.get() != null) {
            LOGGER.warn("Driver session creation requested while this thread already has a session");
            throw new IllegalStateException(
                    "An Android driver session already exists on this thread");
        }

        try {
            LOGGER.info("Creating Android driver session");

            AndroidDriver driver = new AndroidDriver(
                    config.appiumServerUrl(), buildOptions(config));

            DRIVER.set(driver);

            LOGGER.info("Android driver session created successfully");
            return driver;

        } catch (RuntimeException exception) {
            LOGGER.error("Android driver session creation failed ({})",
                    exception.getClass().getSimpleName());
            throw exception;
        }
    }

    public static AndroidDriver getDriver() {
        AndroidDriver driver = DRIVER.get();

        if (driver == null) {
            throw new IllegalStateException(
                    "No Android driver session exists on this thread");
        }

        return driver;
    }

    public static void quitDriver() {
        AndroidDriver driver = DRIVER.get();

        try {
            if (driver != null) {
                LOGGER.info("Terminating Android driver session");

                driver.quit();

                LOGGER.info("Android driver session terminated successfully");
            }
        } catch (RuntimeException exception) {
            LOGGER.error("Android driver session termination failed ({})",
                    exception.getClass().getSimpleName());
            throw exception;
        } finally {
            DRIVER.remove();
        }
    }

    private static UiAutomator2Options buildOptions(FrameworkConfig config) {
        Path apkPath = config.apkPath().toAbsolutePath().normalize();

        if (!Files.isRegularFile(apkPath)) {
            throw new IllegalArgumentException(
                    "APK file not found: " + apkPath);
        }

        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName(config.platformName())
                .setPlatformVersion(config.platformVersion())
                .setDeviceName(config.deviceName())
                .setAutomationName(config.automationName())
                .setApp(apkPath.toString())
                .setAppPackage(config.appPackage())
                .setNoReset(false);

        config.deviceUdid().ifPresent(options::setUdid);
        config.appActivity().ifPresent(options::setAppActivity);
        config.systemPort().ifPresent(options::setSystemPort);

        return options;
    }
}