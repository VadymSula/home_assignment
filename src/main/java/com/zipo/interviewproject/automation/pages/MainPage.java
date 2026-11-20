package com.zipo.interviewproject.automation.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.zipo.interviewproject.automation.gestures.GestureAction.DOWN;
import static com.zipo.interviewproject.automation.gestures.GestureAction.UP;

public final class MainPage extends BasePage {

    private static final String LIST_ID =
            "com.zipo.interviewproject:id/list_rv";

    private static final String ITEM_TITLE_ID =
            "com.zipo.interviewproject:id/my_adapter_item_tv";

    private static final String DOWNLOAD_ID =
            "com.zipo.interviewproject:id/my_adapter_item_btn";

    private static final int MAX_LIST_SCROLLS = 20;
    private static final double SCROLL_PERCENT = 0.5;

    private static final By ITEM_TITLE_LOCATOR = By.id(ITEM_TITLE_ID);
    private static final By DOWNLOAD_LOCATOR = By.id(DOWNLOAD_ID);

    @AndroidFindBy(id = LIST_ID)
    private WebElement earthList;

    @AndroidFindBy(uiAutomator =
            "new UiSelector().resourceId(\"com.zipo.interviewproject:id/action_bar\")"
                    + ".childSelector(new UiSelector().className(\"android.widget.TextView\"))")
    private WebElement toolbarTitle;

    @AndroidFindBy(xpath =
            "//*[@resource-id='" + LIST_ID + "']"
                    + "//*[@resource-id='" + ITEM_TITLE_ID + "']/..")
    private List<WebElement> earthItemRows;

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

    public List<String> getEarthItemTitles() {
        return readEarthItems().stream()
                .map(EarthItem::title)
                .toList();
    }

    public int getEarthItemCount() {
        return getEarthItemTitles().size();
    }

    public int getDownloadButtonCount() {
        return readEarthItems().stream()
                .mapToInt(EarthItem::downloadButtonCount)
                .sum();
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

    private List<EarthItem> readEarthItems() {
        scrollToBeginning();

        List<EarthItem> items = new ArrayList<>(readVisibleItems());

        for (int i = 0; i < MAX_LIST_SCROLLS; i++) {
            boolean canScrollMore =
                    gesture.scroll(LIST_ID, DOWN, SCROLL_PERCENT);

            mergeViewport(items, readVisibleItems());

            if (!canScrollMore) {
                return List.copyOf(items);
            }
        }

        throw new IllegalStateException(
                "Unable to reach the end of the Earth items list");
    }

    private void scrollToBeginning() {
        for (int i = 0; i < MAX_LIST_SCROLLS; i++) {
            if (!gesture.scroll(LIST_ID, UP, SCROLL_PERCENT)) {
                return;
            }
        }

        throw new IllegalStateException(
                "Unable to reach the beginning of the Earth items list");
    }

    private List<EarthItem> readVisibleItems() {
        return earthItemRows.stream()
                .map(row -> new EarthItem(
                        element.getText(row.findElement(ITEM_TITLE_LOCATOR)),
                        row.findElements(DOWNLOAD_LOCATOR).size()))
                .toList();
    }

    private static void mergeViewport(List<EarthItem> collected,
                                      List<EarthItem> visible) {

        int overlap = Math.min(collected.size(), visible.size());

        while (overlap > 0
                && !hasMatchingTitles(collected, visible, overlap)) {
            overlap--;
        }

        if (overlap == 0 && !collected.isEmpty() && !visible.isEmpty()) {
            throw new IllegalStateException(
                    "No overlap between consecutive Earth list viewports");
        }

        for (int i = 0; i < overlap; i++) {
            int index = collected.size() - overlap + i;

            EarthItem previous = collected.get(index);
            EarthItem current = visible.get(i);

            collected.set(index, new EarthItem(
                    previous.title(),
                    Math.max(previous.downloadButtonCount(),
                            current.downloadButtonCount())));
        }

        collected.addAll(visible.subList(overlap, visible.size()));
    }

    private static boolean hasMatchingTitles(List<EarthItem> collected,
                                             List<EarthItem> visible,
                                             int overlap) {

        for (int i = 0; i < overlap; i++) {
            int index = collected.size() - overlap + i;

            if (!collected.get(index).title().equals(visible.get(i).title())) {
                return false;
            }
        }

        return true;
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

    private record EarthItem(String title, int downloadButtonCount) {
    }
}