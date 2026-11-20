package com.zipo.interviewproject.automation.tests;

import com.zipo.interviewproject.automation.base.BaseTest;
import com.zipo.interviewproject.automation.data.TestData;
import com.zipo.interviewproject.automation.pages.MainPage;
import com.zipo.interviewproject.automation.pages.SplashPage;
import io.qameta.allure.Feature;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static io.qameta.allure.Allure.step;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@Feature("Splash Screen")
public class SplashTest extends BaseTest {

    private SplashPage splashPage;
    private MainPage mainPage;

    @BeforeMethod(alwaysRun = true)
    public void initializePages() {
        splashPage = new SplashPage(
                getDriver(), config.explicitWaitTimeout());

        mainPage = new MainPage(
                getDriver(), config.explicitWaitTimeout());
    }

    @Test(description = "Verify application title and loading indicator on Splash Screen")
    public void testSplashScreenContent() {
        step("Launch application and wait for Splash Screen", this::launchApplication);

        step("Verify Splash Screen title and loading indicator", () -> {
            String title = splashPage.getApplicationTitle();
            boolean loadingIndicatorVisible =
                    splashPage.isLoadingIndicatorVisible();

            assertEquals(title, TestData.APPLICATION_TITLE,
                    "Incorrect application title on Splash Screen");

            assertTrue(loadingIndicatorVisible,
                    "Loading indicator should be visible on Splash Screen");
        });
    }

    @Test(description = "Verify transition from Splash Screen to Main Screen")
    public void testSplashToMainScreenTransition() {
        step("Launch application and wait for Splash Screen", this::launchApplication);

        step("Verify transition to Main Screen", () -> {
            mainPage.waitUntilVisible();

            assertFalse(splashPage.isVisible(),
                    "Splash Screen should disappear after Main Screen is displayed");

            assertTrue(mainPage.isVisible(),
                    "Main Screen should be visible after Splash Screen");
        });
    }

    private void launchApplication() {
        device().setWaitForIdleTimeout(0);
        device().relaunchApp(config.appPackage());

        splashPage.waitUntilVisible();
    }
}
