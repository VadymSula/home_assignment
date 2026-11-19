package com.zipo.interviewproject.automation.device;

import com.zipo.interviewproject.automation.waits.WaitAction;
import io.appium.java_client.Setting;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.ScreenOrientation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public final class DeviceAction {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(DeviceAction.class);

    private final AndroidDriver driver;
    private final WaitAction wait;

    public DeviceAction(AndroidDriver driver, Duration timeout) {
        this.driver = driver;
        this.wait = new WaitAction(driver, timeout);
    }

    public void rotateToLandscape() {
        rotateTo(ScreenOrientation.LANDSCAPE);
    }

    public void rotateToPortrait() {
        rotateTo(ScreenOrientation.PORTRAIT);
    }

    public ScreenOrientation getOrientation() {
        return driver.getOrientation();
    }

    public void relaunchApp(String appPackage) {
        LOGGER.info("Relaunching application: {}", appPackage);

        driver.terminateApp(appPackage);
        driver.activateApp(appPackage);
    }

    public void setWaitForIdleTimeout(int milliseconds) {
        LOGGER.debug("Setting wait for idle timeout to {} ms", milliseconds);

        driver.setSetting(Setting.WAIT_FOR_IDLE_TIMEOUT, milliseconds);
    }

    private void rotateTo(ScreenOrientation orientation) {
        if (getOrientation() == orientation) {
            LOGGER.debug("Device is already in {} orientation", orientation);
            return;
        }

        LOGGER.info("Rotating device to {}", orientation);

        driver.rotate(orientation);
        wait.until(ignored -> getOrientation() == orientation);

        LOGGER.info("Device orientation confirmed as {}", orientation);
    }
}