package com.zipo.interviewproject.automation.tests;

import com.zipo.interviewproject.automation.base.BaseTest;
import com.zipo.interviewproject.automation.data.TestData;
import com.zipo.interviewproject.automation.data.TestDataProvider;
import com.zipo.interviewproject.automation.pages.MainPage;
import com.zipo.interviewproject.automation.pages.PaywallPage;
import org.openqa.selenium.ScreenOrientation;
import org.testng.ITestResult;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.function.Consumer;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class PaywallTest extends BaseTest {

    private static final String FIRST_EARTH_ITEM =
            TestData.EARTH_ITEM_TITLES.get(0);

    private MainPage mainPage;
    private PaywallPage paywallPage;

    private boolean restorePortrait;

    @BeforeMethod(alwaysRun = true)
    public void initializePages() {
        if (device() == null) {
            return;
        }

        restorePortrait = false;
        device().rotateToPortrait();

        mainPage = new MainPage(
                getDriver(), config.explicitWaitTimeout());

        paywallPage = new PaywallPage(
                getDriver(), config.explicitWaitTimeout());
    }

    @Test(
            dataProvider = "earthItems",
            dataProviderClass = TestDataProvider.class,
            description = "Verify Paywall content and available actions for every Earth item"
    )
    public void testPaywallContentForEarthItem(String itemTitle) {
        openPaywall(itemTitle);

        assertTrue(paywallPage.isVisible(),
                itemTitle + " Download should open the Paywall");

        SoftAssert softAssert = new SoftAssert();

        softAssert.assertEquals(paywallPage.getTitle(),
                TestData.PAYWALL_TITLE,
                itemTitle + ": Incorrect Paywall title");

        softAssert.assertEquals(paywallPage.getDescription(),
                TestData.PAYWALL_DESCRIPTION,
                itemTitle + ": Incorrect Paywall description");

        checkActionsAvailable(softAssert, itemTitle);

        softAssert.assertAll();

        paywallPage.dismissWithNotNow();

        verifyReturnToMain(itemTitle + " Not Now");
    }

    @Test(
            dataProvider = "paywallActions",
            dataProviderClass = TestDataProvider.class,
            description = "Verify simulated billing actions and corresponding Toast messages"
    )
    public void testPaywallAction(
            String actionName,
            Consumer<PaywallPage> action,
            String expectedToast) {

        openPaywall(FIRST_EARTH_ITEM);

        assertEquals(paywallPage.getDescription(),
                TestData.PAYWALL_DESCRIPTION,
                actionName + " requires the disabled-billing Paywall");

        action.accept(paywallPage);

        paywallPage.waitForToastMessage(expectedToast);

        verifyReturnToMain(actionName);
    }

    @Test(
            dataProvider = "paywallDismissActions",
            dataProviderClass = TestDataProvider.class,
            description = "Verify Paywall dismissal and reopening"
    )
    public void testPaywallDismissalAndReopening(
            String actionName,
            Consumer<PaywallPage> dismissAction) {

        openPaywall(FIRST_EARTH_ITEM);

        dismissAction.accept(paywallPage);

        verifyReturnToMain(actionName);

        openPaywall(FIRST_EARTH_ITEM);

        assertTrue(paywallPage.isVisible(),
                "Paywall should reopen after " + actionName);
    }

    @Test(
            groups = "known-bug",
            description = "BUG-004: Verify Paywall state is retained after device rotation"
    )
    public void testPaywallStatePreservedAfterRotation() {
        openPaywall(FIRST_EARTH_ITEM);

        restorePortrait = true;

        device().rotateToLandscape();

        assertEquals(device().getOrientation(),
                ScreenOrientation.LANDSCAPE,
                "Device should switch to landscape orientation");

        paywallPage.waitUntilVisible();

        assertTrue(paywallPage.isVisible(),
                "Paywall should remain visible after device rotation");
    }

    @Override
    protected void beforeDriverTermination(ITestResult result) {
        if (restorePortrait) {
            restorePortrait = false;
            device().rotateToPortrait();
        }
    }

    private void openPaywall(String itemTitle) {
        mainPage.waitUntilVisible();
        mainPage.clickDownloadButton(itemTitle);
        paywallPage.waitUntilVisible();
    }

    private void verifyReturnToMain(String actionName) {
        paywallPage.waitUntilDisappeared();
        mainPage.waitUntilVisible();

        assertTrue(mainPage.isVisible(),
                actionName + " should close the Paywall and return to Main");
    }

    private void checkActionsAvailable(
            SoftAssert softAssert, String context) {

        softAssert.assertTrue(paywallPage.isSubscribeButtonAvailable(),
                context + ": Subscribe button should be clickable");

        softAssert.assertTrue(paywallPage.isPurchaseButtonAvailable(),
                context + ": Purchase button should be clickable");

        softAssert.assertTrue(paywallPage.isRestorePurchasesButtonAvailable(),
                context + ": Restore Purchases button should be clickable");

        softAssert.assertTrue(paywallPage.isNotNowButtonAvailable(),
                context + ": Not Now button should be clickable");
    }
}