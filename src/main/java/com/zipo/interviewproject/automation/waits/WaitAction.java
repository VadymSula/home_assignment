package com.zipo.interviewproject.automation.waits;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public final class WaitAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(WaitAction.class);
    private final WebDriverWait wait;

    public WaitAction(AppiumDriver driver, Duration timeout) {
        this.wait = new WebDriverWait(driver, timeout);
    }

    public <T> T until(ExpectedCondition<T> condition) {
        return until("custom condition", condition);
    }

    private <T> T until(String description, ExpectedCondition<T> condition) {
        LOGGER.debug("Waiting for {}", description);

        try {
            return wait.until(condition);
        } catch (TimeoutException exception) {
            LOGGER.warn("Wait timed out for {}", description);
            throw exception;
        }
    }

    public WebElement waitForElementToBePresent(By locator) {
        return until("element presence: " + locator,
                ExpectedConditions.presenceOfElementLocated(locator));
    }

    public WebElement waitForElementToBeVisible(WebElement element) {
        return until("element visibility",
                ExpectedConditions.visibilityOf(element));
    }

    public WebElement waitForElementToBeVisible(By locator) {
        return until("element visibility: " + locator,
                ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement waitForElementToBeClickable(WebElement element) {
        return until("element clickability",
                ExpectedConditions.elementToBeClickable(element));
    }

    public WebElement waitForElementToBeClickable(By locator) {
        return until("element clickability: " + locator,
                ExpectedConditions.elementToBeClickable(locator));
    }

    public void waitForElementToBeInvisible(WebElement element) {
        until("element invisibility",
                ExpectedConditions.invisibilityOf(element));
    }

    public void waitForElementToBeInvisible(By locator) {
        until("element invisibility: " + locator,
                ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public void waitForTextToBePresent(WebElement element, String text) {
        until("element text",
                ExpectedConditions.textToBePresentInElement(element, text));
    }

    public void waitForTextToBePresent(By locator, String text) {
        until("element text: " + locator,
                ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }
}