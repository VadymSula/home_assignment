package com.zipo.interviewproject.automation.pages;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;
import java.time.Duration;

public final class SplashPage extends BasePage {

    private static final String APPLICATION_TITLE_ID =
            "com.zipo.interviewproject:id/splash_app_name";

    private static final String LOADING_INDICATOR_ID =
            "com.zipo.interviewproject:id/splash_progress";

    @AndroidFindBy(id = APPLICATION_TITLE_ID)
    private WebElement applicationTitle;

    @AndroidFindBy(id = LOADING_INDICATOR_ID)
    private WebElement loadingIndicator;

    public SplashPage(AndroidDriver driver, Duration explicitWaitTimeout) {
        super(driver, explicitWaitTimeout);
    }

    public String getApplicationTitle() {
        return element.getText(applicationTitle);
    }

    public boolean isLoadingIndicatorVisible() {
        return element.isElementVisible(loadingIndicator);
    }

    public boolean isVisible() {
        return element.isElementVisible(applicationTitle);
    }

    public void waitUntilVisible() {
        wait.waitForElementToBeVisible(applicationTitle);
    }

    public void waitUntilDisappeared() {
        wait.waitForElementToBeInvisible(applicationTitle);
    }

    public Snapshot waitForSnapshot() {
        XMLInputFactory factory = XMLInputFactory.newFactory();

        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        factory.setProperty(
                XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);

        return wait.until(driver ->
                readSnapshot(driver.getPageSource(), factory));
    }

    private Snapshot readSnapshot(String source, XMLInputFactory factory) {
        String title = null;
        boolean titleVisible = false;
        boolean indicatorVisible = false;

        try {
            XMLStreamReader reader = factory.createXMLStreamReader(
                    new StringReader(source));

            try {
                while (reader.hasNext()) {
                    if (reader.next() != XMLStreamConstants.START_ELEMENT) {
                        continue;
                    }

                    String resourceId =
                            reader.getAttributeValue(null, "resource-id");

                    boolean displayed = Boolean.parseBoolean(
                            reader.getAttributeValue(null, "displayed"));

                    if (APPLICATION_TITLE_ID.equals(resourceId) && displayed) {
                        title = reader.getAttributeValue(null, "text");
                        titleVisible = true;
                    }

                    if (LOADING_INDICATOR_ID.equals(resourceId) && displayed) {
                        indicatorVisible = true;
                    }
                }
            } finally {
                reader.close();
            }

        } catch (XMLStreamException exception) {
            throw new IllegalStateException(
                    "Could not read Splash Screen snapshot", exception);
        }

        return titleVisible
                ? new Snapshot(title, indicatorVisible)
                : null;
    }

    public record Snapshot(
            String title,
            boolean loadingIndicatorVisible) {
    }
}