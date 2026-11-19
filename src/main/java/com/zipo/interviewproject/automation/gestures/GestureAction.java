package com.zipo.interviewproject.automation.gestures;

import com.zipo.interviewproject.automation.elements.ElementAction;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public final class GestureAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(GestureAction.class);
    private static final int MAX_SEARCH_SWIPES = 10;
    public static final String LEFT = "left";
    public static final String TOP = "top";
    public static final String WIDTH = "width";

    private final AppiumDriver driver;
    private final ElementAction element;

    public GestureAction(AppiumDriver driver, ElementAction element) {
        this.driver = driver;
        this.element = element;
    }

    public void scrollIntoView(String containerId, String targetSelector) {
        By targetLocator = AppiumBy.androidUIAutomator(targetSelector);

        if (element.isElementVisible(targetLocator)) {
            LOGGER.debug("Target element is already visible. Scrolling skipped");
            return;
        }

        String scrollSelector = (
                "new UiScrollable(new UiSelector().resourceId(\"%s\"))"
                        + ".setMaxSearchSwipes(%d)"
                        + ".scrollIntoView(%s)")
                .formatted(containerId, MAX_SEARCH_SWIPES, targetSelector);

        LOGGER.debug(
                "Scrolling container {} to target {} with up to {} swipes",
                containerId, targetSelector, MAX_SEARCH_SWIPES);

        driver.findElement(AppiumBy.androidUIAutomator(scrollSelector));

        LOGGER.debug("Target element found after scrolling");
    }

    public void tapAbove(WebElement element) {
        Rectangle rect = element.getRect();

        if (!(driver.executeScript("mobile: viewportRect")
                instanceof Map<?, ?> viewport)) {
            throw new IllegalStateException("Unable to retrieve viewport");
        }

        int left = getViewportValue(viewport, LEFT);
        int top = getViewportValue(viewport, TOP);
        int width = getViewportValue(viewport, WIDTH);

        if (rect.getY() <= top) {
            throw new IllegalStateException(
                    "No tappable space above the element");
        }

        int x = left + width / 2;
        int y = top + (rect.getY() - top) / 2;

        tap(x, y);
    }

    private void tap(int x, int y) {
        LOGGER.debug("Tapping screen at coordinates: x={}, y={}", x, y);

        driver.executeScript("mobile: clickGesture",
                Map.of("x", x, "y", y));
    }

    private static int getViewportValue(Map<?, ?> viewport, String key) {
        if (viewport.get(key) instanceof Number value) {
            return value.intValue();
        }

        throw new IllegalStateException("Invalid viewport property: " + key);
    }
}