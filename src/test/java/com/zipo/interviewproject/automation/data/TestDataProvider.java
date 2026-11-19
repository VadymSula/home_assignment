package com.zipo.interviewproject.automation.data;

import com.zipo.interviewproject.automation.pages.PaywallPage;
import org.testng.annotations.DataProvider;

import java.util.function.Consumer;

public final class TestDataProvider {

    private TestDataProvider() {
    }

    @DataProvider(name = "earthItems")
    public static Object[][] earthItems() {
        return TestData.EARTH_ITEM_TITLES.stream()
                .map(title -> new Object[]{title})
                .toArray(Object[][]::new);
    }

    @DataProvider(name = "paywallActions")
    public static Object[][] paywallActions() {
        return new Object[][]{
                {"Subscribe",
                        (Consumer<PaywallPage>) PaywallPage::subscribe,
                        TestData.SUBSCRIBE_TOAST},

                {"Purchase",
                        (Consumer<PaywallPage>) PaywallPage::purchase,
                        TestData.PURCHASE_TOAST},

                {"Restore Purchases",
                        (Consumer<PaywallPage>) PaywallPage::restorePurchases,
                        TestData.RESTORE_PURCHASES_TOAST}
        };
    }

    @DataProvider(name = "paywallDismissActions")
    public static Object[][] paywallDismissActions() {
        return new Object[][]{
                {"Not Now",
                        (Consumer<PaywallPage>) PaywallPage::dismissWithNotNow},

                {"Android Back",
                        (Consumer<PaywallPage>) PaywallPage::dismissWithBack},

                {"Outside Tap",
                        (Consumer<PaywallPage>) PaywallPage::dismissByTappingOutside}
        };
    }
}