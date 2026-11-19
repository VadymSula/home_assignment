package com.zipo.interviewproject.automation.waits;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public final class WaitAction {

    private final WebDriverWait wait;

    public WaitAction(AppiumDriver driver, Duration timeout) {
        this.wait = new WebDriverWait(driver, timeout);
    }

    public WebElement waitForElementToBePresent(By locator) {
        return wait.until(
                ExpectedConditions.presenceOfElementLocated(locator));
    }

    public void waitForTextToBePresent(
            WebElement element, String text) {
        wait.until(
                ExpectedConditions.textToBePresentInElement(element, text));
    }

    public WebElement waitForElementToBeVisible(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    public WebElement waitForElementToBeClickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    public void waitForElementToBeInvisible(WebElement element) {
        wait.until(ExpectedConditions.invisibilityOf(element));
    }
}
