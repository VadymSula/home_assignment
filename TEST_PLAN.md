# Test Plan

## 1. Objective

The objective of testing is to verify the core functionality of the Android application, its stability, and correct user interaction with the available UI elements.

The primary focus is on the critical user flow: application launch, loading the Main Screen, interacting with the list of Earth items, opening the Paywall, and performing the available Paywall actions.

## 2. Test Scope

### Application Under Test

| Parameter | Value |
|---|---|
| Application Name | Interview Project |
| APK | app-debug.apk |
| Package Name | com.zipo.interviewproject |
| Application Version | 1.0 |
| Version Code | 1 |
| Minimum SDK | API 21 |
| Target SDK | API 32 |
| Platform | Android |

Testing is limited to the provided application version.

### In Scope

- Application launch.
- Splash Screen display and transition to the Main Screen.
- Earth items list integrity.
- Download button functionality.
- Paywall display and functionality.
- Toast message validation.
- Navigation between screens.
- Application stability (Crash & ANR).
- Screen orientation changes.
- Application lifecycle.
- Offline behavior.
- Compatibility across different Android versions and screen sizes.

### Out of Scope

- Real purchases and subscriptions.
- Payment processing.
- Restoration of actual purchases.
- Actual content downloading.
- Backend and API testing.
- Performance and security testing.
- Application update testing and cross-version compatibility.

The provided APK contains a test implementation of the Paywall with disabled billing. Therefore, Paywall actions will be validated against the expected simulated behavior.

## 3. Risk Assessment

Testing priorities are determined based on the potential impact of defects on core functionality, application business logic, and user experience.

| Risk | Impact | Priority |
|---|---|---|
| Application fails to launch | Application is unusable | P0 |
| Application crash or ANR | User flow is interrupted | P0 |
| Splash Screen does not disappear | User cannot access the content | P0 |
| Earth items list is empty or inaccessible | Core functionality is blocked | P0 |
| Paywall fails to open for any Earth item | Core functionality and potential monetization are unavailable | P0 |
| Paywall cannot be dismissed | User is blocked | P0 |
| Individual Earth items are missing | Core functionality is partially unavailable | P1 |
| Individual Download buttons do not work | Part of the content is inaccessible for interaction | P1 |
| Incorrect Paywall button behavior | Business logic is affected | P1 |
| Application state loss | User experience is negatively affected | P1 |
| Broken UI after screen orientation changes | Some functionality may become inaccessible | P1 |
| Android version compatibility issues | Functionality is unavailable on certain devices | P1 |
| Incorrect order of Earth items | Content integrity is affected | P2 |
| Visual inconsistencies | Reduced UI quality | P2 |

## 4. Test Strategy

Testing will follow a risk-based approach, combining automated functional testing with manual exploratory testing.

Automation will focus on critical, repeatable scenarios with predictable outcomes that provide the highest value for regression testing.

### Technology Stack

- Java 17
- Appium + UiAutomator2
- TestNG
- Maven
- GitHub Actions

Appium was selected because it enables Android application testing without access to the source code, supports interaction with Android system elements, and allows the framework to be scaled to other mobile applications.

### Automation Principles

- Page Object Model.
- Reusable components.
- Independent test execution.
- Parameterized testing.
- Explicit waits instead of fixed sleeps.
- Configuration-driven execution.
- Multi-device execution.
- Meaningful assertions.
- Automatic failure diagnostics.

## 5. Automation Coverage

### Splash Screen

- Verify that the Splash Screen is displayed during a cold launch.
- Verify successful transition to the Main Screen.

### Main Screen

- Verify the presence of all 11 Earth items.
- Verify that there are no duplicate items.
- Verify the correct order of items (Earth 1 — Earth 11).
- Verify the presence and availability of the Download button for each item.
- Verify that the Paywall opens for each Earth item.

### Paywall

- Verify the Paywall title and description.
- Verify that all buttons are present and enabled.
- Verify the Subscribe action and its corresponding Toast message.
- Verify the Purchase action and its corresponding Toast message.
- Verify the Restore Purchases action and its corresponding Toast message.
- Verify that the Paywall is dismissed after each of these actions.

### Navigation

- Verify Paywall dismissal using Not Now.
- Verify Paywall dismissal using the Android Back button.
- Verify Paywall dismissal by tapping outside the dialog.
- Verify that the Paywall can be reopened after dismissal.
- Verify that the Main Screen remains accessible after interacting with the Paywall.

### Application Stability & Failure Diagnostics

The following mechanisms will be implemented at the framework level:

- Application crash detection.
- Application Not Responding (ANR) detection.
- Logcat analysis within the current test execution scope.
- Screenshot capture on test failure.
- Logcat collection on test failure.

Any detected crash or ANR of the application under test during test execution must result in a test failure, regardless of the outcome of functional assertions.

### AI Integration — AI Failure Analyzer

An AI Failure Analyzer will be implemented at the framework level to automate the analysis of test failures.

Core functionality:

- Analysis of exceptions, stack traces, page source, and relevant Logcat entries.
- Failure classification (Application Bug, Locator Issue, Assertion Failure, Environment Issue, Crash, ANR, Unknown).
- Identification of the likely root cause of test failures.
- Generation of troubleshooting recommendations.
- Storage of AI analysis results as a separate test artifact.

The purpose of this integration is to reduce the time spent on failure analysis and simplify defect localization.

AI analysis results will be advisory only. The AI agent will not modify locators, test code, assertions, or test execution results.

## 6. Manual & Exploratory Testing

The following scenarios will remain outside the primary automation coverage:

| Scenario | Reason |
|---|---|
| Screen orientation changes | Visual assessment of UI elements and their accessibility |
| Background / Foreground | Exploration of application lifecycle behavior |
| Rapid repeated taps | Exploration of unexpected application behavior |
| Offline behavior | Limited automation value due to disabled billing |
| UI element alignment | Visual assessment of the user interface |
| System font size changes | Verification of UI adaptability |

Automation of these scenarios may be added in the future based on exploratory testing results, identified risks, and defect frequency.

## 7. Test Environment

Two Android emulators with different operating system versions and screen sizes are planned for application compatibility testing.

| Parameter | Primary Device | Secondary Device |
|---|---|---|
| Device Profile | Google Pixel 7 | Google Pixel 4a |
| Android Version | Android 14 | Android 16 |
| API Level | 34 | 36 |
| Resolution | 1080 × 2400 | 1080 × 2340 |
| Display Size | 6.3″ | 5.8″ |
| System Image | Google Play | Google Play |
| Google Play Account | Signed out | Signed out |
| Language | English (US) | English (US) |
| Default Orientation | Portrait | Portrait |
| Network | Enabled | Enabled |
| Execution | Local / CI | CI |

The primary device will be used for framework development, local test execution, and regression testing.

The secondary device will be used for compatibility testing.

The framework configuration must allow the test environment to be changed without modifying the test code.

Automated tests will be executed locally and in GitHub Actions. The GitHub Actions Matrix Strategy is planned for parallel test execution across different devices, with independent test environments and separate failure artifacts, without relying on third-party device farms.

The priority is to achieve stable test execution on the primary device before extending coverage to the secondary device within the available timebox.

## 8. Exit Criteria

Test coverage will be considered sufficient when:

- All critical P0 risks are covered by automated tests.
- Major functional P1 risks are covered by automated tests.
- The critical user flow is fully covered.
- All 11 Earth items have been verified for interaction.
- All Paywall actions have been verified.
- All known Paywall dismissal methods have been verified.
- Crash and ANR detection mechanisms are implemented at the framework level.
- Failure diagnostics are available for every failed test.
- The AI Failure Analyzer is implemented and validated.
- Tests can be executed independently.
- Repeated test executions demonstrate consistent results.
- The test suite can be executed locally and in CI.
- Identified defects are documented.
- Coverage gaps and limitations are documented.

Additional criteria:

- Test execution across multiple Android API levels.
- Parallel test execution in CI.
- Test report generation as a CI artifact.

Coverage sufficiency is determined by risk and functional coverage rather than the number of automated tests or the absence of application defects.

Any scenarios that cannot be implemented within the 7-hour timebox will be documented, along with their priorities and recommendations for further test coverage expansion.