package com.zipo.interviewproject.automation.elements;

import com.zipo.interviewproject.automation.waits.WaitAction;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Quotes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ToastAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ToastAction.class);
    private static final String TOAST_CLASS = "android.widget.Toast";

    private final WaitAction wait;

    public ToastAction(WaitAction wait) {
        this.wait = wait;
    }

    public void waitForToastMessage(String text) {
        By locator = AppiumBy.xpath(
                "//" + TOAST_CLASS + "[@text=" + Quotes.escape(text) + "]");

        wait.waitForElementToBePresent(locator);

        LOGGER.debug("Successfully detected expected Toast message");
    }
}