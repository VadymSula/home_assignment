package com.zipo.interviewproject.automation.pages;

import com.zipo.interviewproject.automation.elements.ElementAction;
import com.zipo.interviewproject.automation.elements.ToastAction;
import com.zipo.interviewproject.automation.gestures.GestureAction;
import com.zipo.interviewproject.automation.waits.WaitAction;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;

public abstract class BasePage {

    private final AppiumDriver driver;
    protected final ElementAction element;
    protected final WaitAction wait;
    protected final ToastAction toast;
    protected final GestureAction gesture;

    protected BasePage(AppiumDriver driver, Duration explicitWaitTimeout) {
        this.driver = driver;
        this.element = new ElementAction(driver);
        this.wait = new WaitAction(driver, explicitWaitTimeout);
        this.toast = new ToastAction(wait);
        this.gesture = new GestureAction(driver, element);
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ZERO), this);
    }

    protected AppiumDriver getDriver() {
        return driver;
    }

    protected void pressBack() {
        driver.navigate().back();
    }
}
