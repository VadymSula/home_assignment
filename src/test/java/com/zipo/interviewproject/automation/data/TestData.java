package com.zipo.interviewproject.automation.data;

import java.util.List;

public final class TestData {

    public static final String APPLICATION_TITLE = "Interview Project";
    public static final List<String> EARTH_ITEM_TITLES = List.of(
            "Earth 1", "Earth 2", "Earth 3", "Earth 4", "Earth 5", "Earth 6",
            "Earth 7", "Earth 8", "Earth 9", "Earth 10", "Earth 11");
    public static final int EARTH_ITEM_COUNT = EARTH_ITEM_TITLES.size();
    public static final String PAYWALL_TITLE = "Premium";
    public static final String PAYWALL_DESCRIPTION =
            "Test paywall only. Billing is disabled; buttons show a short message and do not start a download.";
    public static final String SUBSCRIBE_TOAST = "Test paywall: subscribe (no-op)";
    public static final String PURCHASE_TOAST = "Test paywall: purchase (no-op)";
    public static final String RESTORE_PURCHASES_TOAST = "Test paywall: restore (no-op)";

    private TestData() {
    }
}
