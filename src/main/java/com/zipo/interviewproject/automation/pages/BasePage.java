package com.zipo.interviewproject.automation.pages;

import com.zipo.interviewproject.automation.elements.ElementAction;
import com.zipo.interviewproject.automation.waits.WaitAction;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;

/**
 * Shared interactions for Appium page objects.
 * Declare static Android elements as {@link WebElement} fields with {@link AndroidFindBy}
 * and build dynamic {@link By} locators with {@link AppiumBy}.
 */
public abstract class BasePage {

    private final AppiumDriver driver;
    protected final ElementAction element;
    protected final WaitAction wait;

    protected BasePage(AppiumDriver driver, Duration explicitWaitTimeout) {
        this.driver = driver;
        this.element = new ElementAction(driver);
        this.wait = new WaitAction(driver, explicitWaitTimeout);
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ZERO), this);
    }

    protected AppiumDriver getDriver() {
        return driver;
    }

    protected void pressBack() {
        driver.navigate().back();
    }
}
