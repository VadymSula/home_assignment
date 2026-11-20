# Bug Reports

## Test Environment

- **Application:** Interview Project
- **Build:** app-debug.apk
- **Device:** Google Pixel 7 (Android Emulator)
- **OS:** Android 14 (API 34)
- **Testing type:** Manual Exploratory Testing
- **Date:** November 19, 2026

---

## BUG-001 — "Not Now" button is partially clipped on the Paywall in landscape mode

**Severity:** Medium  
**Priority:** P2  
**Type:** UI / Usability  
**Status:** Open

### Preconditions

- The application is installed and launched.
- Screen rotation is enabled.
- The device is in portrait orientation.

### Steps to Reproduce

1. Navigate to the Main Screen.
2. Rotate the device to landscape orientation.
3. Tap the Download button of any Earth item.
4. Verify that the Paywall is displayed.
5. Observe the "Not Now" button.

### Expected Result

The Paywall adapts to landscape orientation. All available actions, including the "Not Now" button, remain fully visible or accessible through scrolling.

### Actual Result

The "Not Now" button is partially clipped in landscape orientation. Scrolling does not reveal the remaining portion of the button. However, the visible portion remains clickable.

### Impact

Reduced usability and accessibility of the dedicated Paywall dismissal button.

### Workaround

Tap the visible portion of the "Not Now" button, use the Android Back button, or tap outside the dialog.

### Notes

The defect is covered by manual testing. Standard Appium visibility and clickability checks pass because the button remains partially visible and interactive. Visual regression testing could be introduced in the future to detect similar layout issues automatically.

---

## BUG-002 — Application remains in landscape mode after rotating the device to reverse portrait orientation

**Severity:** Medium (provisional)  
**Priority:** P2  
**Type:** UI / Screen Orientation  
**Status:** Requires Verification

### Preconditions

- The application is installed and launched.
- Screen rotation is enabled.
- The device is in portrait orientation.

### Steps to Reproduce

1. Launch the application.
2. Rotate the device to landscape orientation.
3. Rotate the device to reverse portrait orientation (180 degrees from the initial portrait orientation).
4. Observe the application layout.

### Expected Result

The application updates its layout according to the device orientation, provided that reverse portrait orientation is supported.

### Actual Result

The application retains its landscape layout after the device is rotated to reverse portrait orientation.

### Impact

The application layout may not correspond to the physical device orientation, potentially affecting usability.

### Notes

Additional investigation is required to determine whether this behavior is caused by application-level orientation restrictions, Android system behavior, or emulator configuration.

---

## BUG-003 — Download buttons for Earth 10 and Earth 11 are horizontally misaligned

**Severity:** Low  
**Priority:** P3  
**Type:** UI / Cosmetic  
**Status:** Open

### Preconditions

- The application is installed and launched.
- The Main Screen is displayed.
- The device is in portrait orientation.

### Steps to Reproduce

1. Navigate to the Main Screen.
2. Observe the Download buttons associated with Earth 1 through Earth 9.
3. Compare their horizontal alignment with the Download buttons associated with Earth 10 and Earth 11.

### Expected Result

All Download buttons are consistently aligned, regardless of the length of the corresponding Earth item title.

### Actual Result

The Download buttons associated with Earth 10 and Earth 11 are shifted horizontally to the right compared to the buttons associated with Earth 1 through Earth 9.

### Impact

Visual inconsistency across the Main Screen. No functional impact was observed.

---

## BUG-004 — Paywall is dismissed after changing the device orientation

**Severity:** Medium  
**Priority:** P2  
**Type:** Functional / Screen Orientation  
**Status:** Open

### Preconditions

- The application is installed and launched.
- Screen rotation is enabled.
- The device is in portrait orientation.

### Steps to Reproduce

1. Navigate to the Main Screen.
2. Tap the Download button of any Earth item.
3. Verify that the Paywall is displayed.
4. Rotate the device to landscape orientation.
5. Observe the application state.

### Expected Result

The Paywall remains displayed and adapts its layout to landscape orientation without losing its current state.

### Actual Result

The Paywall is dismissed automatically, and the Main Screen is displayed.

### Impact

Changing the device orientation interrupts the user's interaction with the Paywall.

### Workaround

Tap the Download button again to reopen the Paywall.

### Notes

The defect is covered by the automated regression test `testPaywallStatePreservedAfterRotation` in `PaywallTest.java` and is consistently reproduced during test execution.