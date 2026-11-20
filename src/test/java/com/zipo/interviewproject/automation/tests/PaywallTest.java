package com.zipo.interviewproject.automation.tests;

import com.zipo.interviewproject.automation.base.BaseTest;
import com.zipo.interviewproject.automation.data.TestData;
import com.zipo.interviewproject.automation.data.TestDataProvider;
import com.zipo.interviewproject.automation.pages.MainPage;
import com.zipo.interviewproject.automation.pages.PaywallPage;
import io.qameta.allure.Feature;
import io.qameta.allure.Param;
import io.qameta.allure.model.Parameter.Mode;
import org.openqa.selenium.ScreenOrientation;
import org.testng.ITestResult;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.function.Consumer;

import static io.qameta.allure.Allure.step;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Feature("Paywall")
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

        step("Verify Paywall content and available actions", () -> {
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
        });

        step("Dismiss Paywall with Not Now", paywallPage::dismissWithNotNow);

        verifyReturnToMain(itemTitle + " Not Now");
    }

    @Test(
            dataProvider = "paywallActions",
            dataProviderClass = TestDataProvider.class,
            description = "Verify simulated billing actions and corresponding Toast messages"
    )
    public void testPaywallAction(
            String actionName,
            @Param(excluded = true, mode = Mode.HIDDEN) Consumer<PaywallPage> action,
            String expectedToast) {

        openPaywall(FIRST_EARTH_ITEM);

        assertEquals(paywallPage.getDescription(),
                TestData.PAYWALL_DESCRIPTION,
                actionName + " requires the disabled-billing Paywall");

        step("Execute billing action " + actionName, () -> action.accept(paywallPage));
        step("Verify billing Toast", () -> paywallPage.waitForToastMessage(expectedToast));

        verifyReturnToMain(actionName);
    }

    @Test(
            dataProvider = "paywallDismissActions",
            dataProviderClass = TestDataProvider.class,
            description = "Verify Paywall dismissal and reopening"
    )
    public void testPaywallDismissalAndReopening(
            String actionName,
            @Param(excluded = true, mode = Mode.HIDDEN) Consumer<PaywallPage> dismissAction) {

        openPaywall(FIRST_EARTH_ITEM);

        step("Dismiss Paywall with " + actionName, () -> dismissAction.accept(paywallPage));

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

        step("Rotate device to landscape", () -> {
            device().rotateToLandscape();
            assertEquals(device().getOrientation(),
                    ScreenOrientation.LANDSCAPE,
                    "Device should switch to landscape orientation");
        });

        step("Verify Paywall state after rotation", () -> {
            paywallPage.waitUntilVisible();

            assertTrue(paywallPage.isVisible(),
                    "Paywall should remain visible after device rotation");
        });
    }

    @Override
    protected void beforeDriverTermination(ITestResult result) {
        if (restorePortrait) {
            restorePortrait = false;
            device().rotateToPortrait();
        }
    }

    private void openPaywall(String itemTitle) {
        step("Open Paywall for " + itemTitle, () -> {
            mainPage.waitUntilVisible();
            mainPage.clickDownloadButton(itemTitle);
            paywallPage.waitUntilVisible();
        });
    }

    private void verifyReturnToMain(String actionName) {
        step("Verify return to Main Screen", () -> {
            paywallPage.waitUntilDisappeared();
            mainPage.waitUntilVisible();

            assertTrue(mainPage.isVisible(),
                    actionName + " should close the Paywall and return to Main");
        });
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
