package com.zipo.interviewproject.automation.diagnostics;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.OutputType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Saves a screenshot of the current Appium session. */
final class ScreenshotCapture {

    private ScreenshotCapture() {
    }

    static void capture(AndroidDriver driver, Path destination) throws IOException {
        Path screenshot = driver.getScreenshotAs(OutputType.FILE).toPath();
        Files.move(screenshot, destination);
    }
}
