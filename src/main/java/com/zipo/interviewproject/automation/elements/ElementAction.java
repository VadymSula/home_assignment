package com.zipo.interviewproject.automation.elements;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public final class ElementAction {

    private final AppiumDriver driver;

    public ElementAction(AppiumDriver driver) {
        this.driver = driver;
    }

    public List<WebElement> findElements(WebElement parent, By locator) {
        return parent.findElements(locator);
    }

    public void click(WebElement element) {
        element.click();
    }

    public String getText(WebElement element) {
        return element.getText();
    }

    public boolean isElementVisible(By locator) {
        return matchesNow(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public boolean isElementVisible(WebElement element) {
        return matchesNow(ExpectedConditions.visibilityOf(element));
    }

    public boolean isElementPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    public boolean isElementClickable(WebElement element) {
        return matchesNow(ExpectedConditions.elementToBeClickable(element));
    }

    private boolean matchesNow(ExpectedCondition<WebElement> condition) {
        try {
            return condition.apply(driver) != null;
        } catch (NoSuchElementException | StaleElementReferenceException exception) {
            return false;
        }
    }
}
