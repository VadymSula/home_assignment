package com.zipo.interviewproject.automation.driver;

import com.zipo.interviewproject.automation.config.FrameworkConfig;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import java.nio.file.Files;
import java.nio.file.Path;

/** Owns the Android driver session for each test execution thread. */
public final class DriverFactory {

    private static final ThreadLocal<AndroidDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {
    }

    public static AndroidDriver createDriver(FrameworkConfig config) {
        if (DRIVER.get() != null) {
            throw new IllegalStateException("An Android driver session already exists on this thread");
        }

        AndroidDriver driver = new AndroidDriver(config.appiumServerUrl(), buildOptions(config));
        DRIVER.set(driver);
        return driver;
    }

    public static AndroidDriver getDriver() {
        AndroidDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("No Android driver session exists on this thread");
        }
        return driver;
    }

    public static void quitDriver() {
        AndroidDriver driver = DRIVER.get();
        try {
            if (driver != null) {
                driver.quit();
            }
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
                .setApp(config.apkPath().toAbsolutePath().normalize().toString())
                .setAppPackage(config.appPackage())
                .setNoReset(false);

        config.deviceUdid().ifPresent(options::setUdid);
        config.appActivity().ifPresent(options::setAppActivity);
        config.systemPort().ifPresent(options::setSystemPort);
        return options;
    }
}
