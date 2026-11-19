package com.zipo.interviewproject.automation.pages;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

import java.time.Duration;

public final class SplashPage extends BasePage {

    @AndroidFindBy(id = "com.zipo.interviewproject:id/splash_app_name")
    private WebElement applicationTitle;

    @AndroidFindBy(id = "com.zipo.interviewproject:id/splash_progress")
    private WebElement loadingIndicator;

    public SplashPage(AndroidDriver driver, Duration explicitWaitTimeout) {
        super(driver, explicitWaitTimeout);
    }

    public String getApplicationTitle() {
        return element.getText(applicationTitle);
    }

    public boolean isLoadingIndicatorVisible() {
        return element.isElementVisible(loadingIndicator);
    }

    public boolean isVisible() {
        return element.isElementVisible(applicationTitle);
    }

    public void waitUntilVisible() {
        wait.waitForElementToBeVisible(applicationTitle);
    }

    public void waitUntilDisappeared() {
        wait.waitForElementToBeInvisible(applicationTitle);
    }
}
