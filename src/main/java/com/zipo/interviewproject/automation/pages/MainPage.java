package com.zipo.interviewproject.automation.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;

public final class MainPage extends BasePage {

    private static final String LIST_ID = "com.zipo.interviewproject:id/list_rv";
    private static final String ITEM_TITLE_ID = "com.zipo.interviewproject:id/my_adapter_item_tv";
    private static final String DOWNLOAD_ID = "com.zipo.interviewproject:id/my_adapter_item_btn";

    @AndroidFindBy(id = LIST_ID)
    private WebElement earthList;

    @AndroidFindBy(uiAutomator = "new UiSelector().resourceId(\"com.zipo.interviewproject:id/action_bar\")"
            + ".childSelector(new UiSelector().className(\"android.widget.TextView\"))")
    private WebElement toolbarTitle;

    @AndroidFindBy(id = ITEM_TITLE_ID)
    private List<WebElement> earthItemTitles;

    @AndroidFindBy(id = DOWNLOAD_ID)
    private List<WebElement> downloadButtons;

    public MainPage(AndroidDriver driver, Duration explicitWaitTimeout) {
        super(driver, explicitWaitTimeout);
    }

    public boolean isVisible() {
        return element.isElementVisible(earthList);
    }

    public void waitUntilVisible() {
        wait.waitForElementToBeVisible(earthList);
    }

    public String getToolbarTitle() {
        return element.getText(toolbarTitle);
    }

    /**
     * Returns item titles available in the current UI hierarchy.
     */
    public List<String> getEarthItemTitles() {
        return earthItemTitles.stream()
                .map(element::getText)
                .toList();
    }

    /**
     * Counts item titles available in the current UI hierarchy.
     */
    public int getEarthItemCount() {
        return earthItemTitles.size();
    }

    /**
     * Counts Download buttons available in the current UI hierarchy.
     */
    public int getDownloadButtonCount() {
        return downloadButtons.size();
    }

    public boolean isDownloadButtonPresent(String itemTitle) {
        return element.isElementPresent(downloadButtonFor(itemTitle));
    }

    public boolean isDownloadButtonVisible(String itemTitle) {
        return element.isElementVisible(downloadButtonFor(itemTitle));
    }

    public boolean isDownloadButtonAvailable(String itemTitle) {
        return element.isElementClickable(downloadButtonFor(itemTitle));
    }

    public void scrollToEarthItem(String itemTitle) {
        gesture.scrollIntoView(LIST_ID, earthTitleSelector(itemTitle));
    }

    public void clickDownloadButton(String itemTitle) {
        scrollToEarthItem(itemTitle);

        element.click(wait.waitForElementToBeClickable(
                downloadButtonFor(itemTitle)));
    }

    private static By downloadButtonFor(String itemTitle) {
        return AppiumBy.androidUIAutomator(
                "%s.fromParent(new UiSelector().resourceId(\"%s\"))"
                        .formatted(earthTitleSelector(itemTitle), DOWNLOAD_ID));
    }

    private static String earthTitleSelector(String itemTitle) {
        String escapedTitle = itemTitle
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");

        return "new UiSelector().resourceId(\"%s\").text(\"%s\")"
                .formatted(ITEM_TITLE_ID, escapedTitle);
    }
}