package com.zipo.interviewproject.automation.base;

import com.zipo.interviewproject.automation.config.FrameworkConfig;
import com.zipo.interviewproject.automation.driver.DriverFactory;
import io.appium.java_client.android.AndroidDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/** Gives each TestNG test invocation its own Android session. */
public abstract class BaseTest {

    protected final FrameworkConfig config = FrameworkConfig.load();

    @BeforeMethod(alwaysRun = true)
    protected final void startDriverSession() {
        DriverFactory.createDriver(config);
    }

    @AfterMethod(alwaysRun = true)
    protected final void stopDriverSession() {
        DriverFactory.quitDriver();
    }

    protected final AndroidDriver driver() {
        return DriverFactory.getDriver();
    }
}
