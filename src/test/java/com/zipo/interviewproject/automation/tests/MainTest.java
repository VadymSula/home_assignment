package com.zipo.interviewproject.automation.tests;

import com.zipo.interviewproject.automation.base.BaseTest;
import com.zipo.interviewproject.automation.data.TestData;
import com.zipo.interviewproject.automation.pages.MainPage;
import io.qameta.allure.Feature;
import org.openqa.selenium.NoSuchElementException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.HashSet;
import java.util.List;

import static io.qameta.allure.Allure.step;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Feature("Main Screen")
public class MainTest extends BaseTest {

    private MainPage mainPage;

    @BeforeMethod(alwaysRun = true)
    public void initializePage() {
        mainPage = new MainPage(
                getDriver(), config.explicitWaitTimeout());
    }

    @Test(description = "Verify Main Screen content")
    public void testMainScreenContent() {
        step("Verify the Main Screen is visible after launch", () -> {
            mainPage.waitUntilVisible();
            assertTrue(mainPage.isVisible(),
                    "Main Screen should be visible after launch");
        });

        step("Verify the toolbar title", () ->
                assertEquals(mainPage.getToolbarTitle(),
                        TestData.APPLICATION_TITLE,
                        "Incorrect Main Screen toolbar title"));
    }

    @Test(description = "Verify Earth items count, titles, order and uniqueness")
    public void testEarthItemsList() {
        mainPage.waitUntilVisible();

        List<String> titles = mainPage.getEarthItemTitles();

        SoftAssert softAssert = new SoftAssert();

        softAssert.assertEquals(titles.size(),
                TestData.EARTH_ITEM_COUNT,
                "Incorrect Earth item count");

        softAssert.assertEquals(titles,
                TestData.EARTH_ITEM_TITLES,
                "Earth item titles or their order do not match");

        softAssert.assertEquals(new HashSet<>(titles).size(),
                titles.size(),
                "Earth item titles should be unique");

        softAssert.assertAll();
    }

    @Test(description = "Verify Download button availability for every Earth item")
    public void testDownloadButtonsAvailability() {
        mainPage.waitUntilVisible();

        SoftAssert softAssert = new SoftAssert();

        softAssert.assertEquals(mainPage.getDownloadButtonCount(),
                TestData.EARTH_ITEM_COUNT,
                "Incorrect Download button count");

        for (String title : TestData.EARTH_ITEM_TITLES) {
            try {
                mainPage.scrollToEarthItem(title);
            } catch (NoSuchElementException exception) {
                softAssert.fail(
                        title + " could not be located in the Earth items list",
                        exception);
                continue;
            }

            softAssert.assertTrue(mainPage.isDownloadButtonPresent(title),
                    title + " Download button should be present");

            softAssert.assertTrue(mainPage.isDownloadButtonVisible(title),
                    title + " Download button should be visible");

            softAssert.assertTrue(mainPage.isDownloadButtonAvailable(title),
                    title + " Download button should be clickable");
        }

        softAssert.assertAll();
    }
}