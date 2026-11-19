package com.zipo.interviewproject.automation.pages;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

import java.time.Duration;

public final class PaywallPage extends BasePage {

    private static final String TITLE_ID = "com.zipo.interviewproject:id/paywall_title";

    @AndroidFindBy(id = TITLE_ID)
    private WebElement title;

    @AndroidFindBy(id = "com.zipo.interviewproject:id/paywall_message")
    private WebElement description;

    @AndroidFindBy(id = "com.zipo.interviewproject:id/paywall_btn_subscribe")
    private WebElement subscribeButton;

    @AndroidFindBy(id = "com.zipo.interviewproject:id/paywall_btn_purchase")
    private WebElement purchaseButton;

    @AndroidFindBy(id = "com.zipo.interviewproject:id/paywall_btn_restore")
    private WebElement restorePurchasesButton;

    @AndroidFindBy(id = "com.zipo.interviewproject:id/paywall_btn_not_now")
    private WebElement notNowButton;

    @AndroidFindBy(xpath = "//android.widget.FrameLayout[parent::hierarchy"
            + " and .//*[@resource-id='" + TITLE_ID + "']]")
    private WebElement dialogWindow;

    public PaywallPage(AndroidDriver driver, Duration explicitWaitTimeout) {
        super(driver, explicitWaitTimeout);
    }

    public boolean isVisible() {
        return element.isElementVisible(title);
    }

    public void waitUntilVisible() {
        wait.waitForElementToBeVisible(title);
    }

    public void waitUntilDisappeared() {
        wait.waitForElementToBeInvisible(title);
    }

    public String getTitle() {
        return element.getText(title);
    }

    public String getDescription() {
        return element.getText(description);
    }

    public boolean isSubscribeButtonAvailable() {
        return element.isElementClickable(subscribeButton);
    }

    public boolean isPurchaseButtonAvailable() {
        return element.isElementClickable(purchaseButton);
    }

    public boolean isRestorePurchasesButtonAvailable() {
        return element.isElementClickable(restorePurchasesButton);
    }

    public boolean isNotNowButtonAvailable() {
        return element.isElementClickable(notNowButton);
    }

    public void subscribe() {
        clickWhenReady(subscribeButton);
    }

    public void purchase() {
        clickWhenReady(purchaseButton);
    }

    public void restorePurchases() {
        clickWhenReady(restorePurchasesButton);
    }

    public void dismissWithNotNow() {
        clickWhenReady(notNowButton);
    }

    public void dismissWithBack() {
        waitUntilVisible();
        pressBack();
    }

    public void dismissByTappingOutside() {
        waitUntilVisible();
        gesture.tapAbove(dialogWindow);
    }

    public void waitForToastMessage(String text) {
        toast.waitForToastMessage(text);
    }

    private void clickWhenReady(WebElement button) {
        element.click(wait.waitForElementToBeClickable(button));
    }
}
