package com.zipo.interviewproject.automation.elements;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class ElementAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElementAction.class);
    private final AppiumDriver driver;

    public ElementAction(AppiumDriver driver) {
        this.driver = driver;
    }

    public List<WebElement> findElements(WebElement parent, By locator) {
        LOGGER.debug("Finding child elements by locator: {}", locator);
        return parent.findElements(locator);
    }

    public List<WebElement> findElements(By locator) {
        LOGGER.debug("Finding elements by locator: {}", locator);
        return driver.findElements(locator);
    }

    public void click(WebElement element) {
        LOGGER.debug("Clicking element");
        element.click();
    }

    public String getText(WebElement element) {
        LOGGER.debug("Reading element text");
        return element.getText();
    }

    public boolean isElementVisible(By locator) {
        return matchesNow(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public boolean isElementVisible(WebElement element) {
        return matchesNow(ExpectedConditions.visibilityOf(element));
    }

    public boolean isElementPresent(By locator) {
        return !findElements(locator).isEmpty();
    }

    public boolean isElementClickable(WebElement element) {
        return matchesNow(ExpectedConditions.elementToBeClickable(element));
    }

    public boolean isElementClickable(By locator) {
        return matchesNow(ExpectedConditions.elementToBeClickable(locator));
    }

    private boolean matchesNow(ExpectedCondition<WebElement> condition) {
        try {
            return condition.apply(driver) != null;
        } catch (NoSuchElementException | StaleElementReferenceException exception) {
            return false;
        }
    }
}