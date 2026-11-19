## Prompt #01 — Core Framework Infrastructure

**Date:** 2026-11-19

**Context:**

Initialize the core infrastructure of the Android test automation framework according to TEST_PLAN.md and the architectural requirements defined in AGENTS.md.

**Prompt:**

Act as a Senior QA Automation Engineer with expertise in Java and Appium.

Analyze the current project structure, read TEST_PLAN.md, and follow the instructions specified in AGENTS.md.

Implement the core infrastructure of an automation framework for Android application testing.

### 1. Technology Stack

Use the following technology versions:

| Component          | Version |
| ------------------ | ------- |
| Java               | 17      |
| Appium Server      | 3.x     |
| Appium Java Client | 10.1.1  |
| Selenium           | 4.44.0  |
| UiAutomator2       | 8.6.0   |
| TestNG             | 7.x     |
| Android            | 14 / 16 |
| Android API Level  | 34 / 36 |
| Build Tool         | Maven   |

### 2. Maven Configuration

- Create pom.xml.
- Use Java 17.
- Add Appium Java Client 10.1.1.
- Add Selenium 4.44.0.
- Add TestNG using the latest stable 7.x version.
- Pin specific versions of all dependencies.
- Configure Maven Compiler Plugin and Maven Surefire Plugin.
- Download all required dependencies.
- Check for dependency conflicts, including transitive dependencies.
- Do not add dependencies that are not required at this stage.

Appium Server and UiAutomator2 are test environment components, not Maven dependencies.

### 3. Project Structure

Create a standard Maven project structure and the following packages:

- base
- config
- driver
- pages
- tests
- utils
- listeners
- ai

Separate test resources from Java source code.

### 4. Git Configuration

Create a .gitignore file.

Exclude the following from Git:

- IntelliJ IDEA configuration files.
- Maven build artifacts.
- Logs.
- Test execution artifacts.
- Local configuration files containing sensitive data.

### 5. Configuration Management

Implement centralized framework configuration management.

The configuration must support the following parameters:

- Appium Server URL.
- Device Name.
- Device UDID.
- Platform Name.
- Platform Version.
- Automation Name.
- APK Path.
- Application Package.
- Application Activity.
- Explicit Wait Timeout.
- UiAutomator2 System Port.

Use a properties file to store the default configuration, with the ability to override parameters through JVM System Properties.

Configure the primary test environment according to TEST_PLAN.md.

Do not use absolute local paths. Do not invent configuration values that cannot be verified.

### 6. Driver Factory

Implement DriverFactory to manage Appium Driver sessions.

Core requirements:

- Use AndroidDriver and UiAutomator2Options.
- Build capabilities based on the centralized configuration.
- Provide methods for creating, retrieving, and terminating Driver Sessions.
- Ensure Driver Session independence.
- Consider future support for parallel test execution.
- Ensure proper resource cleanup after session termination.
- Use W3C-compatible capabilities.
- Do not use deprecated APIs.

### 7. BasePage

Implement BasePage following the Page Object Model pattern using Appium PageFactory.

Core requirements:

- Use @AndroidFindBy as the primary approach for declaring static UI elements.
- Use AppiumBy for dynamic locators.
- Use PageFactory and AppiumFieldDecorator for centralized Page Object initialization within BasePage.
- Use WebElement.
- Do not use @CacheLookup.
- Allow future extension of Page Objects to support iOS.

BasePage must provide reusable methods for UI interactions:

- Find element.
- Find elements.
- Click.
- Get text.
- Check element visibility.
- Check element availability.
- Wait for element visibility.
- Wait for element to be clickable.
- Wait for element invisibility.

Support both WebElement and By locators in the corresponding reusable methods.

Use WebDriverWait and ExpectedConditions.

Consider the internal AppiumFieldDecorator timeout to avoid cumulative timeouts and unpredictable waiting behavior when combining PageFactory with explicit waits.

Do not use Thread.sleep() or combine implicit waits with explicit waits.

### 8. BaseTest

Implement BaseTest for centralized test lifecycle management.

- Use TestNG annotations.
- Initialize a Driver Session before each test.
- Terminate the Driver Session after each test, regardless of the execution result.
- Ensure test independence.
- Consider future integration of Failure Diagnostics and TestNG Listeners before Driver Session termination.

### General Requirements

- Follow SOLID, DRY, and KISS principles.
- Avoid overengineering and unnecessary abstractions.
- Use clear and consistent naming conventions.
- Ensure Separation of Concerns.
- All class names, method names, variable names, and comments must be in English.
- Do not modify TEST_PLAN.md or AGENTS.md.
- Do not create application-specific Page Objects.
- Do not implement automated tests.
- Do not implement Failure Diagnostics, AI Failure Analyzer, or CI/CD at this stage.
- Do not commit or push any changes without my approval.

### Validation

After completing the implementation:

- Validate the Maven configuration.
- Analyze the dependency tree.
- Execute `mvn clean test-compile`.
- Resolve any identified compilation errors.
- Present the final project structure.
- Provide a brief description of the implemented classes and their responsibilities.
- Explain the architectural decisions.

Do not proceed to the next implementation stage without my approval.

**Outcome:**

- Initialized the Maven project structure.
- Configured Java 17 and the required automation dependencies.
- Centralized Selenium dependency version management using Selenium BOM.
- Implemented immutable framework configuration using Java records.
- Implemented configuration overrides through JVM System Properties.
- Implemented DriverFactory with ThreadLocal-based session management.
- Implemented BasePage with PageFactory initialization, reusable element interactions, and explicit waits.
- Introduced BaseTest for centralized test lifecycle management.
- Prepared the package structure for future framework extensions.

**Validation:**

- Reviewed Maven configuration and dependency declarations.
- Reviewed core framework components.
- Runtime validation on an Android emulator is pending.

**Human Review & Comments:**

*Reviewed*
 
TO DO:
- Separate the Framework Layer from the Test Layer.
- Add missing element interaction methods and move element-related functionality out of the BasePage class.
- Rename classes to improve naming clarity and consistency
---
## Prompt #02 — Framework and Test Layer Separation

**Date:** 2026-11-19

**Context:**

During the initial framework implementation review, all framework components were found under src/test/java. A decision was made to separate the Framework Layer from the Test Layer to improve maintainability, reusability, and scalability.

**Prompt:**

Refactor the current project structure to clearly separate the Framework Layer from the Test Layer.

- Move all framework-related classes and packages to src/main/java.
- Keep only automated test classes and test scenarios in src/test/java.
- Move framework configuration resources to src/main/resources.
- Use src/test/resources for test-specific resources.
- Preserve the existing package naming conventions.
- Update Maven dependency scopes where necessary to support the new project structure.
- Preserve the existing functionality and implementation logic.
- Do not introduce new functionality or unnecessary abstractions.
- Do not implement application-specific Page Objects or automated tests yet.

Apply this structure to all existing and future framework components, including BaseTest, BasePage, DriverFactory, Configuration Management, Page Objects, Listeners, Utilities, and AI Failure Analyzer.

After refactoring, execute `mvn clean test-compile`, resolve any compilation issues, and present the updated project structure.

Do not commit or push any changes without my approval.

Document this task in AI_PROMPTS.md according to the AI-Assisted Development Documentation rules defined in AGENTS.md.

**Outcome:**

- Separated the Framework Layer from the Test Layer.
- Moved framework components to src/main/java.
- Moved framework configuration resources to src/main/resources.
- Reserved src/test/java for automated tests.
- Updated Maven dependency scopes.
- Preserved existing framework functionality and package naming conventions.

**Validation:**

- Reviewed the updated project structure.
- Reviewed Maven dependency scopes.

**Human Review & Comments:**

*Reviewed*

TO DO
- To separate methods by ElementAction and WaitAction
---
## Prompt #03 — Separation of Element Interactions and Explicit Waits

**Date:** 2026-11-19

**Context:**

During the framework code review, an opportunity was identified to improve Separation of Concerns by moving element interactions and explicit wait management out of BasePage into dedicated components.

This architectural decision was made to improve maintainability, readability, and reusability while following the Single Responsibility Principle.

**Prompt:**

Refactor the existing BasePage to improve Separation of Concerns and follow the Single Responsibility Principle.

Separate element interactions and explicit waits into two independent components.

### ElementAction

Create an ElementAction class within the elements package.

Responsibilities:

- Element search.
- Element interactions.
- Element state checks.

ElementAction must not contain explicit waits or implement any implicit waiting logic.

### WaitAction

Create a WaitAction class within the waits package.

Responsibilities:

- Centralized explicit wait management.
- Element visibility, invisibility, presence, and clickability waits.
- Text-related waits.

### BasePage

BasePage must be responsible only for:

- PageFactory initialization.
- Driver access.
- ElementAction and WaitAction initialization.
- Common screen-level operations.

Use composition to integrate ElementAction and WaitAction into BasePage.

### Requirements

- Preserve the existing @AndroidFindBy and AppiumFieldDecorator approach.
- Use WebElement as the primary type for element interactions.
- Use By locators for dynamic element searches where necessary.
- Avoid duplicated methods and unnecessary abstractions.
- Keep explicit waits transparent and controlled by Page Objects.
- Preserve existing functionality.
- Do not introduce additional dependencies.

After refactoring, verify compilation and explain the architectural changes.

Do not commit or push any changes without my approval.

Document this task in AI_PROMPTS.md according to the AI-Assisted Development Documentation rules defined in AGENTS.md.

**Outcome:**

- Separated element interactions from explicit wait management.
- Introduced ElementAction for element search, interactions, and immediate state checks.
- Introduced WaitAction for centralized explicit wait management.
- Reduced BasePage responsibilities.
- Applied Composition over Inheritance.
- Established explicit control over waiting behavior within Page Objects.
- Preserved the existing PageFactory approach.

**Changed Files:**

- `ElementAction.java`
- `WaitAction.java`
- `BasePage.java`

**Validation:**

- Architectural review of component responsibilities.
- Runtime validation on an Android emulator is pending.

**Human Review & Comments:**

*Reviewed and approved. No changes required*

---

### Prompt #04 — Android Application Exploration and Page Objects

**Date:** 2026-11-19

**Context:**

Explore the supplied APK through Appium on the local emulator, then implement the Splash, Main, and Paywall Page Objects using the observed UI hierarchy.

**Prompt:**

Act as a Senior QA Automation Engineer with expertise in Java and Appium.

Analyze TEST_PLAN.md, AGENTS.md, and the existing framework implementation before making any changes.

### 1. Application Exploration

Explore the Android application using the locally running Appium Server and Android emulator.

- Use the existing framework configuration where possible.
- Use Appium getPageSource() to analyze the actual UI hierarchy.
- Verify resource IDs, content descriptions, element attributes, and parent-child relationships.
- Explore all three application screens and their available interactions.
- Capture the Splash Screen through a controlled application relaunch within an active Appium session.
- Verify the behavior of all Paywall actions, including transient Toast messages.
- Do not perform real purchases.
- Do not invent locators or assume application behavior without verification.

If an element cannot be inspected or verified, report it as a limitation.

### 2. Page Objects

Implement the following Page Objects within the existing pages package.

**SplashPage**

- Application title.
- Loading indicator.
- Splash Screen identification.
- Splash Screen visibility and disappearance.
- Support for verifying the transition to the Main Screen.

**MainPage**

- Main Screen identification.
- Toolbar title.
- Earth items collection.
- Earth item titles.
- Download buttons collection.
- Ability to retrieve all Earth item titles.
- Ability to retrieve the number of Earth items and Download buttons.
- Ability to check Download button visibility and availability.
- Ability to click the Download button associated with a specific Earth item.

Do not declare individual WebElement fields for every Earth item. Use collections and parameterized locators where appropriate.

**PaywallPage**

- Paywall identification.
- Title.
- Description.
- Subscribe button.
- Purchase button.
- Restore Purchases button.
- Not Now button.
- Toast messages.
- Paywall visibility and disappearance.
- Methods for interacting with all available Paywall actions.
- Ability to dismiss the Paywall using Android Back and by tapping outside the dialog.

### 3. Locator Strategy

Prioritize locator strategies in the following order:

1. Resource ID.
2. Accessibility ID.
3. Android UIAutomator.
4. Class Name, where sufficiently specific.
5. XPath, only when more reliable alternatives are unavailable.

Additional requirements:

- Use @AndroidFindBy for statically declared UI elements.
- Use WebElement and List&lt;WebElement&gt; fields.
- Use AppiumBy for dynamic locators where necessary.
- Prefer stable, unique, and descriptive locators.
- Avoid absolute XPath, hardcoded coordinates, and fragile positional locators.
- Verify locator uniqueness where applicable.
- Ensure that locators do not depend on a specific screen resolution or device model.

### 4. Framework Integration

- Extend the existing BasePage.
- Reuse ElementAction and WaitAction.
- Keep explicit waits transparent and controlled by Page Objects.
- Do not duplicate existing framework functionality.
- Keep locators private and expose meaningful public methods.
- Keep assertions outside Page Objects.
- Return actual application data rather than hardcoded expected values.
- Do not use Thread.sleep(), implicit waits, or @CacheLookup.
- Avoid unnecessary abstractions.
- Follow SOLID, DRY, KISS, and Clean Code principles.

### 5. Implementation Scope

Implement only the Page Object Layer at this stage.

- Do not implement automated test scenarios yet.
- Do not implement Failure Diagnostics, AI Failure Analyzer, or CI/CD.
- Do not introduce additional dependencies.
- Do not modify existing framework components unless strictly necessary.
- Clean up temporary exploration scripts and Appium sessions created during this task.
- Do not commit or push any changes without my approval.

### 6. Validation

After implementation:

- Verify the implemented locators against the actual application.
- Execute `mvn clean test-compile`.
- Resolve compilation issues.
- Provide a summary of the implemented Page Objects and their public methods.
- Explain non-trivial locator decisions.
- Report any discovered application issues, limitations, and unverified elements.

Do not proceed to automated test implementation without my approval.

Document this task in AI_PROMPTS.md according to the AI-Assisted Development Documentation rules defined in AGENTS.md.[@SplashPage.java](file:///Users/vadymsula/IdeaProjects/home_assignment/src/main/java/com/zipo/interviewproject/automation/pages/SplashPage.java)

**Outcome:**

- Implemented SplashPage, MainPage, and PaywallPage with the existing BasePage, ElementAction, and WaitAction.
- Used screen-specific resource IDs and scoped toolbar and row selectors. MainPage reads all rows by traversing the RecyclerView and leaves the list at the top.
- Added Paywall interactions and dismissal using Not Now, Android Back, or an outside tap calculated from live window bounds and system bars. Toast texts are read from Appium XML.

**Changed Files:**

- `src/main/java/com/zipo/interviewproject/automation/pages/SplashPage.java`
- `src/main/java/com/zipo/interviewproject/automation/pages/MainPage.java`
- `src/main/java/com/zipo/interviewproject/automation/pages/PaywallPage.java`
- `AI_PROMPTS.md`
- `artifacts/ui-exploration/splash.png` (local ignored artifact)

**Validation:**

- Explored the matching checkout APK on a Pixel 7 emulator running Android 14 / API 34 with Appium 3.9.1. Checked resource IDs, content descriptions, attributes, and relationships in `getPageSource()` and verified the selectors in live Appium sessions.
- Captured the Splash title and loading indicator during a controlled relaunch in an active session and verified the transition to Main. A transient Splash title lookup raced the transition during an initial run. Reducing the idle timeout in an exploration session allowed the Page Object to read transient Splash state. The screenshot was captured while both Splash IDs were present.
- Retrieved 11 Earth titles, 11 rows, and 11 Download buttons in portrait and landscape. All 11 Download buttons opened the Paywall in portrait. Verified scrolling to the offscreen Earth 11 in landscape.
- Subscribe, Purchase, and Restore Purchases each dismissed the Paywall and produced their respective simulated no-op Toasts. Verified dismissal by Not Now, Android Back, and tapping outside. The outside tap worked in both orientations.
- UiAutomator2 exposed Toast nodes in XML. Element commands on those nodes returned stale references, and the retained texts sometimes included the app label. Android 16 / API 36 was outside the observed environment.
- Corrected the initial chained-locator annotation type mismatch. The final `mvn clean test-compile` passed on JDK 17. Confirmed all exploration sessions were closed and portrait orientation was restored. Removed temporary exploration scripts and captures from the scratch directory.

**Human Review & Comments:**

*Reviewed*

TO DO:

- Extract Toast-related functionality into a dedicated ToastAction class.
- Extract gesture-related functionality into a dedicated GestureAction class.
- Improve Toast message synchronization to ensure reliable detection and retrieval of transient messages.
---

### Prompt #05 — Paywall Toast and Gesture Refactoring

**Date:** 2026-11-19

**Context:**

Move reusable Toast and gesture behavior out of the Paywall Page Object and synchronize retrieval with Toast detection.

**Prompt:**

Refactor the existing PaywallPage to improve Separation of Concerns, reusability, and maintainability.

- Extract Toast-related functionality into a dedicated ToastAction class within the elements package.
- Extract gesture-related functionality into a dedicated GestureAction class within the gestures package.
- Integrate both components into BasePage using composition.
- Improve Toast message synchronization to ensure reliable detection and retrieval of transient messages.
- Preserve existing functionality and verified locators.
- Follow the existing architectural and naming conventions.
- Avoid unnecessary abstractions and additional dependencies.
- Keep Page Objects focused on screen-specific behavior.

After refactoring, verify compilation and validate the affected functionality against the running application.

Do not commit or push any changes without my approval.

Document this task in AI_PROMPTS.md according to the AI-Assisted Development Documentation rules defined in AGENTS.md.[@BaseTest.java](file:///Users/vadymsula/IdeaProjects/home_assignment/src/main/java/com/zipo/interviewproject/automation/base/BaseTest.java)

**Outcome:**

- Composed `ToastAction` and `GestureAction` in `BasePage` and delegated Paywall operations to them. Preserved the Paywall control and dialog locators.
- Synchronized exact-text detection and retrieval by returning the immutable Appium XML snapshot that satisfied the wait. The immediate getter still reads the current UI. A matching Toast still retained from an earlier action can satisfy the wait.

**Changed Files:**

- `src/main/java/com/zipo/interviewproject/automation/elements/ToastAction.java`
- `src/main/java/com/zipo/interviewproject/automation/gestures/GestureAction.java`
- `src/main/java/com/zipo/interviewproject/automation/pages/BasePage.java`
- `src/main/java/com/zipo/interviewproject/automation/pages/PaywallPage.java`
- `src/main/java/com/zipo/interviewproject/automation/waits/WaitAction.java`
- `AI_PROMPTS.md`

**Validation:**

- `mvn clean test-compile` passed on JDK 17.
- On the local Pixel 7 emulator, verified exact Toast detection for Subscribe, Purchase, and Restore Purchases, and confirmed the returned snapshot was still readable after the live Toast expired.
- Verified outside dismissal in portrait and landscape, restored the original orientation, and closed the temporary Appium session.

**Human Review & Comments:**
*Reviewed*

TO DO:

- To refactor an XML-based variant of the method to work with Toast.

---

### Prompt #06 — Centralized Framework Logging

**Date:** 2026-11-19

**Context:**

Configure a single logging provider and instrument the framework's lifecycle and operations.

**Prompt:**

Integrate centralized logging into the existing mobile test automation framework using SLF4J and Logback.

### Requirements

- Add compatible SLF4J and Logback dependencies to pom.xml.
- Configure logging through logback.xml in src/main/resources.
- Resolve the existing SLF4J provider warning.
- Use parameterized logging instead of string concatenation.
- Use private static final Logger fields.
- Include timestamps, log levels, thread names, and class names in the logging output.

### Logging Scope

Add meaningful logging to the existing framework components:

- FrameworkConfig
- DriverFactory
- BaseTest
- ElementAction
- WaitAction
- GestureAction
- ToastAction

### Logging Levels

- DEBUG — element interactions, explicit waits, gesture operations, and Toast detection.
- INFO — test execution lifecycle, driver session creation and termination.
- WARN — recoverable issues and unexpected but non-critical behavior.
- ERROR — critical framework failures.

### Constraints

- Do not introduce unnecessary logging abstractions or custom logger wrappers.
- Avoid excessive and duplicate logging.
- Do not log individual polling attempts during explicit waits.
- Do not log sensitive information.
- Do not suppress exceptions.
- Preserve existing functionality and architectural conventions.
- Do not modify Page Objects unless necessary.
- Do not implement automated test scenarios, failure diagnostics, or AI integration at this stage.
- Follow SOLID, DRY, KISS, YAGNI, and Clean Code principles.

### Validation

- Verify dependency compatibility.
- Ensure that only one SLF4J provider is present.
- Execute `mvn clean test-compile`.
- Verify logging output during an actual application session.
- Report all changed files, validation results, and identified limitations.

Do not commit or push any changes without my approval.

Document this task in AI_PROMPTS.md according to the AI-Assisted Development Documentation rules defined in AGENTS.md.[@AI_PROMPTS.md](file:///Users/vadymsula/IdeaProjects/home_assignment/AI_PROMPTS.md)

**Outcome:**

- Pinned SLF4J 2.0.19 and Logback 1.6.8. Configured timestamped console output with INFO as the framework default and DEBUG available through `framework.log.level`.
- Added parameterized framework logging at operation boundaries. Failure messages omit exception payloads and existing exceptions still propagate.

**Changed Files:**

- `pom.xml`
- `src/main/resources/logback.xml`
- `src/main/java/com/zipo/interviewproject/automation/config/FrameworkConfig.java`
- `src/main/java/com/zipo/interviewproject/automation/driver/DriverFactory.java`
- `src/main/java/com/zipo/interviewproject/automation/base/BaseTest.java`
- `src/main/java/com/zipo/interviewproject/automation/elements/ElementAction.java`
- `src/main/java/com/zipo/interviewproject/automation/waits/WaitAction.java`
- `src/main/java/com/zipo/interviewproject/automation/gestures/GestureAction.java`
- `src/main/java/com/zipo/interviewproject/automation/elements/ToastAction.java`
- `AI_PROMPTS.md`

**Validation:**

- Maven resolved the pinned versions without dependency conflicts. `mvn clean test-compile` passed on JDK 17.
- The resolved runtime classpath exposed one SLF4J provider, Logback. The live run produced no provider warning.
- Initial UI checks timed out while an emulator launcher error dialog blocked Main. Dismissing the dialog and explicitly relaunching the supplied APK allowed the live check to complete.
- Verified timestamp, level, thread, and class output across all seven components during a driver lifecycle, simulated Subscribe Toast, and outside tap. Also confirmed WARN and ERROR output for duplicate session creation and invalid configuration, with the original exceptions propagated. The configuration sentinel, emulator serial, and Toast payload did not appear in the log.
- Closed the temporary session and removed the local validation harness. Live verification used the Android 14 emulator and console output.

**Human Review & Comments:**

*Reviewed*

TO DO:
- Remove a code duplication
- Add test results status for logging
- Add more information about locators for logging

---

### Prompt #07 — Automated Test Suite Implementation

**Date:** 2026-11-20

**Context:**

Implement the Splash, Main, and Paywall suite and verify the landscape regression against the supplied APK.

**Prompt:**

# Automated Test Suite Implementation

Act as a Senior QA Automation Engineer with strong expertise in Java, Appium, TestNG, mobile testing, and test automation architecture.

Analyze the existing project structure, framework implementation, TEST_PLAN.md, AGENTS.md, AI_PROMPTS.md, and BUG_REPORTS.md (if available) before making any changes.

Implement a maintainable, reliable, and independent automated test suite using the existing framework.

## 1. General Requirements

- Reuse the existing framework components and Page Objects.
- Follow the existing architectural and naming conventions.
- Maintain a clear separation between the Framework Layer and the Test Layer.
- Prioritize native Appium, Selenium, TestNG, and Java APIs over custom implementations.
- Do not implement custom solutions when equivalent built-in functionality already exists.
- Follow SOLID, DRY, KISS, YAGNI, and Clean Code principles.
- Avoid premature optimization and unnecessary abstractions.
- Do not introduce additional dependencies unless strictly necessary.

## 2. Test Layer Structure

Create the following test classes:

- SplashTest.java
- MainTest.java
- PaywallTest.java

Create a dedicated data package within the Test Layer:

- TestData.java — reusable test data and expected values.
- TestDataProvider.java — centralized TestNG DataProviders.

Keep application-specific expected values outside the Framework Layer.

## 3. SplashTest

Implement two test methods.

### testSplashScreenContent()

Verify that:

- The Splash Screen is displayed after a cold application launch.
- The application title matches the expected value.
- The loading indicator is displayed.

### testSplashToMainScreenTransition()

Verify that:

- The Splash Screen appears after a cold application launch.
- The Splash Screen disappears.
- The Main Screen becomes visible within the configured explicit wait timeout.

Use a controlled application relaunch within an already established Appium session.

Prefer native Appium application lifecycle methods, such as terminateApp() and activateApp(), provided that their behavior is suitable for capturing the Splash Screen.

Verify the actual timing and behavior of the Splash Screen. Account for its transient nature without using fixed sleeps, modifying the application, or introducing artificial delays.

## 4. MainTest

Implement three test methods.

### testMainScreenContent()

Verify that:

- The Main Screen is displayed.
- The toolbar title matches the expected value.

### testEarthItemsList()

Verify that:

- Exactly 11 Earth items are present.
- All item titles match the expected values.
- Items are displayed in the correct order, from Earth 1 to Earth 11.
- No duplicate items are present.

Use SoftAssert where appropriate to report multiple independent discrepancies within the same logical scenario.

Preserve the existing simplified RecyclerView implementation.

The primary test environment is expected to display all 11 Earth items. Verify this assumption before relying on it. Do not reintroduce complex RecyclerView traversal algorithms.

### testDownloadButtonsAvailability()

Verify that:

- Exactly 11 Download buttons are present.
- Every expected Earth item has an associated Download button.
- Each Download button is visible and enabled.

Include the corresponding Earth item title in assertion messages.

## 5. PaywallTest

Implement five test methods.

### testPaywallOpensForEarthItem(String itemTitle)

Use a DataProvider containing all 11 Earth item titles.

Verify that:

- The Download button associated with the specified Earth item can be clicked.
- The Paywall opens successfully.

Each Earth item must be tested independently.

### testPaywallContent()

Verify that:

- The Paywall is displayed.
- The title matches the expected value.
- The description matches the expected value.
- The Subscribe button is available.
- The Purchase button is available.
- The Restore Purchases button is available.
- The Not Now button is available.

Use SoftAssert for independent content and button availability checks.

### testPaywallAction(action, expectedToast)

Use a DataProvider covering:

- Subscribe
- Purchase
- Restore Purchases

Prefer Java method references and Consumer<PaywallPage> to avoid duplicated test logic and unnecessary conditional statements.

Include a readable action name in each DataProvider entry.

Verify that:

- The selected Paywall action can be performed.
- The expected Toast message appears.
- The Paywall is dismissed.
- The Main Screen becomes accessible again.

Verify the exact Toast messages against the actual application before defining expected values.

Use the existing ToastAction implementation.

Check the Toast immediately after performing the corresponding action, before waiting for the Paywall to disappear.

Do not perform real purchases or attempt to bypass the intentionally disabled billing functionality.

### testPaywallDismissalAndReopening(dismissAction)

Use a DataProvider covering:

- Not Now
- Android Back
- Outside Tap

Prefer Java method references and Consumer<PaywallPage>.

Verify that:

- The selected dismissal action closes the Paywall.
- The Main Screen becomes accessible again.
- The Paywall can be reopened successfully.

Each dismissal mechanism must be tested independently.

Reuse the existing GestureAction implementation for outside tapping.

### testPaywallActionsAvailableInLandscapeMode()

Implement a regression test for BUG-001, where the Not Now button is not visible in landscape orientation.

Test scenario:

1. Open the Paywall in portrait orientation.
2. Rotate the device to landscape orientation.
3. Wait until the requested orientation is applied.
4. Verify that the Paywall remains displayed.
5. Verify that all Paywall actions remain accessible, including Not Now.

Before finalizing the test, investigate whether the missing button is accessible through scrolling.

If the button is accessible through scrolling, adjust the test and report the actual behavior rather than producing a false failure.

If the defect is confirmed:

- Assign the test to the TestNG group "known-bug".
- Use meaningful assertions that describe the actual functional or usability issue.
- Reference BUG-001 in the test description.
- Preserve the genuine failing test result.
- Do not use artificial Assert.fail() statements.
- Do not weaken assertions to make the test pass.
- Do not disable the test.

The device must return to portrait orientation regardless of the test outcome.

## 6. DeviceAction

Extend the Framework Layer with a dedicated DeviceAction class within the device package.

Responsibilities:

- Rotate the device to landscape orientation.
- Rotate the device to portrait orientation.
- Retrieve the current screen orientation.
- Support controlled application relaunch for Splash Screen testing.

Use native Appium APIs wherever available, including:

- rotate()
- getOrientation()
- terminateApp()
- activateApp()

Verify API compatibility with the currently installed Appium Java Client and UiAutomator2 versions.

Use explicit waits to synchronize orientation changes where necessary.

Do not implement custom coordinate-based rotation, shell commands, or custom polling mechanisms when supported Appium APIs can achieve the same result.

Integrate DeviceAction into BaseTest using the existing composition and dependency management conventions.

Add meaningful logging using the existing SLF4J and Logback configuration.

Do not add unrelated device functionality.

## 7. DataProviders and Test Data

Implement three centralized DataProviders:

- earthItems — 11 entries.
- paywallActions — 3 entries.
- paywallDismissActions — 3 entries.

Maintain readable and descriptive parameterized test executions.

Keep expected application titles, descriptions, item names, counts, and Toast messages in the Test Layer.

Expected values must not be dynamically derived from the actual application state during test execution.

Do not introduce external JSON, CSV, or YAML test data files for this small test suite.

Disable parallel DataProvider execution for the current single-emulator environment.

## 8. Test Lifecycle Management

Preserve the existing BaseTest lifecycle:

- @BeforeMethod — create an independent AndroidDriver session for each test invocation.
- @AfterMethod — terminate the driver session and log the test result.

Use test-class-level @BeforeMethod methods to initialize the required Page Objects and prepare the appropriate initial application state.

### SplashTest

Initialize SplashPage and MainPage.

Do not wait for the Main Screen before performing the controlled application relaunch within the test.

### MainTest

Initialize MainPage.

Ensure that the Main Screen loading verification is performed within the relevant test so that an application loading failure is reported as a test failure rather than merely a skipped test.

### PaywallTest

Initialize MainPage and PaywallPage.

Prepare the Main Screen as the initial state.

Do not open the Paywall in @BeforeMethod because opening it is part of the scenarios under test.

Ensure orientation cleanup is performed before driver termination, even when the landscape test fails.

Cleanup failures must not replace or hide the original test failure.

Keep the teardown extensible so that future failure diagnostics can capture the failing application state before orientation restoration and driver termination.

Do not implement failure diagnostics at this stage.

Do not replace per-test sessions with shared class-level sessions.

## 9. Test Quality Requirements

- Use TestNG assertions.
- Follow the Arrange, Act, Assert pattern.
- Use descriptive test method names and test descriptions.
- Include meaningful assertion messages.
- Use Hard Assertions for mandatory preconditions.
- Use SoftAssert for logically related independent validations.
- Always call assertAll() when using SoftAssert.
- Keep tests independent and individually executable.
- Do not introduce test execution order dependencies.
- Do not use dependsOnMethods.
- Do not use Thread.sleep() or fixed delays.
- Do not use automatic test retries to hide instability.
- Do not expose locators or low-level driver operations within test classes.
- Reuse existing Page Objects and Framework Layer components.
- Preserve existing logging conventions.
- Avoid unnecessary inheritance, helper classes, and design patterns.

## 10. Expected Test Coverage

Target the following test suite:

- SplashTest: 2 test methods, 2 executions.
- MainTest: 3 test methods, 3 executions.
- PaywallTest: 5 test methods, 19 executions.

Total:

- 3 test classes.
- 10 test methods.
- 24 test executions.
- 23 regular functional test executions.
- 1 known-defect regression test, subject to defect confirmation.

Support running the regular suite separately from the known-defect test through TestNG groups.

Verify the following Maven commands if BUG-001 is confirmed:

- mvn clean test -DexcludedGroups=known-bug
- mvn clean test -Dgroups=known-bug
- mvn clean test

The complete suite must report the genuine failure of a confirmed known defect.

## 11. Validation

After implementation:

- Execute mvn clean test-compile.
- Resolve compilation issues.
- Run the regular automated test suite against the configured Android emulator.
- Run the known-defect test separately.
- Investigate unexpected failures and potential flaky behavior.
- Verify that individual test methods can be executed independently.
- Verify that application sessions are properly terminated.
- Verify that the device returns to portrait orientation after the landscape test.
- Verify that temporary exploration code and validation scripts are removed.

Provide a final implementation summary containing:

- Created and modified files.
- Implemented test methods.
- Test execution results.
- Confirmed application defects.
- Unexpected failures.
- Identified limitations.
- Architectural decisions and trade-offs.
- Any deviations from the requested implementation.

Report actual validation results only.

## 12. Implementation Scope

Implement only the Automated Test Suite and the minimal framework extensions required to support it.

Do not implement:

- Failure Diagnostics.
- Crash and ANR Detection.
- AI Failure Analyzer.
- GitHub Actions.
- Allure integration.
- Network testing.
- Additional device or OS configurations.

Do not refactor existing framework components unless required for test functionality.

Do not commit or push any changes without my approval.

Document this task in AI_PROMPTS.md according to the AI-Assisted Development Documentation rules defined in AGENTS.md.[@MainPage.java](file:///Users/vadymsula/IdeaProjects/home_assignment/src/main/java/com/zipo/interviewproject/automation/pages/MainPage.java)

**Outcome:**

- Added the Splash, Main, and Paywall TestNG classes, with expectations and three serial data providers in the Test Layer. The existing standalone Toast probe was retained and excluded from the default suite.
- Added `DeviceAction` with Appium cold relaunch and synchronized rotation. Added a pre-termination hook to `BaseTest` so portrait restoration runs with a live session and cleanup errors cannot overwrite an existing test failure.
- Linked the landscape scenario to BUG-001 and grouped it as `known-bug`. Rotating the open dialog returned to Main. After reopening in landscape, Not Now had only a 21-pixel visible strip. An in-dialog scroll did not reveal more of it, and tapping the exposed portion did dismiss the reopened dialog.

**Changed Files:**

- `src/test/java/com/zipo/interviewproject/automation/tests/SplashTest.java`
- `src/test/java/com/zipo/interviewproject/automation/tests/MainTest.java`
- `src/test/java/com/zipo/interviewproject/automation/tests/PaywallTest.java`
- `src/test/java/com/zipo/interviewproject/automation/data/TestData.java`
- `src/test/java/com/zipo/interviewproject/automation/data/TestDataProvider.java`
- `src/main/java/com/zipo/interviewproject/automation/device/DeviceAction.java`
- `src/main/java/com/zipo/interviewproject/automation/base/BaseTest.java`
- `src/main/java/com/zipo/interviewproject/automation/pages/MainPage.java`
- `pom.xml`
- `AI_PROMPTS.md`

**Validation:**

- `mvn clean test-compile` passed on JDK 17. Powered the suite with Appium 3.9.1, Appium Java Client 10.1.1, and UiAutomator2 8.6.0 on the preconfigured Pixel 7 Android 14 emulator.
- In live exploration, native activation returned in about 0.63–0.74 seconds and the next observable Splash hierarchy contained the title and displayed spinner. Verified 11 titles and Download buttons in the portrait viewport and confirmed the three exact simulated action Toasts before setting the expected values.
- The first regular run passed 22 scenarios before an ADB API-level timeout blocked session creation for the final test. The first group-only run also exposed TestNG's group filtering of the Paywall initializer. The initializer now runs under group filtering and checks that the driver session was created.
- Later attempts encountered UiAutomator2 accessibility timeouts and a blocking Android System UI dialog. Recovered the emulator and Appium service, then cleared the system dialog. Main readiness was moved from Paywall configuration into each scenario to avoid a cascade of configuration skips.
- `mvn clean test` executed all 24 cases and reported 21 passes, the genuine BUG-001 failure, and two Main readiness timeouts before the Not Now and Earth 8 actions. All 24 sessions quit successfully.
- The corrected `mvn clean test -Dgroups=known-bug` executed one case and failed because rotation closed the open Paywall. Confirmed portrait restoration before successful session termination. Both runs ended with Android rotation set to portrait and an empty ADB forward list.
- Ran `SplashTest#testSplashScreenContent`, `MainTest#testEarthItemsList`, and `PaywallTest#testPaywallContent` separately. All three passed after emulator recovery.
- The final `mvn clean test -DexcludedGroups=known-bug` passed all 23 cases, including the scenarios that timed out in the full run. All 23 sessions quit successfully, the ADB forward list was empty, and the emulator remained in portrait.
- Validation covered the primary Android 14 emulator. Removed the temporary exploration and validation scripts.

**Human Review & Comments:**

*Reviewed*

TO DO

- Refactor changes in Main Page according to simpler implementation
- Remove a duplicated logic for tests

---

### Prompt #08 — TestNG Suite Configuration

**Date:** 2026-11-20

**Context:**

Add named TestNG suite entry points for the current mobile tests.

**Prompt:**

# TestNG Suite Configuration

Act as a Senior QA Automation Engineer experienced in Java, TestNG, Appium, Maven, and CI/CD.

Review the existing test structure, pom.xml, AGENTS.md, and AI_PROMPTS.md.

## Objective

Introduce TestNG XML suites to provide centralized and flexible test execution management for local development and future CI/CD integration.

## Requirements

Create the following files under `src/test/resources/suites/`:

### regression.xml

- Suite name: Mobile Regression Suite.
- Include all functional test classes.
- Exclude the `known-bug` group.
- Expected test executions: 22.

### known-bugs.xml

- Suite name: Known Bugs Suite.
- Include only tests belonging to the `known-bug` group.
- Expected test executions: 1.

### full-suite.xml

- Suite name: Full Mobile Test Suite.
- Include all automated tests without group exclusions.
- Expected test executions: 23.

## Implementation Guidelines

- Use native TestNG XML configuration.
- Use package-based test discovery instead of explicitly listing test classes.
- Disable parallel execution because the current environment uses a single Android emulator.
- Preserve existing test isolation and lifecycle management.
- Ensure compatibility with the existing TestNG and Maven Surefire versions.
- Use standard TestNG XML conventions.
- Keep the configuration simple and extensible.
- Do not introduce additional dependencies, Maven profiles, or custom execution mechanisms.
- Do not modify pom.xml unless strictly necessary.
- Preserve the ability to execute individual test classes and methods.
- Preserve the existing `known-bug` group behavior.

## Execution Commands

Ensure the suites can be executed using the following commands:

Regression suite:

`mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/suites/regression.xml`

Known bugs suite:

`mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/suites/known-bugs.xml`

Full suite:

`mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/suites/full-suite.xml`

## Validation

- Validate the XML configuration.
- Verify suite discovery and group filtering.
- Verify compatibility with Maven Surefire.
- Verify that existing TestNG lifecycle methods are executed correctly under group filtering.
- Execute `mvn clean test-compile`.
- Avoid unnecessary full test executions at this stage. Full runtime validation will be performed separately.

## Scope Restrictions

- Do not modify existing test scenarios.
- Do not refactor framework components.
- Do not attempt to fix the existing SplashTest instability as part of this task.
- Do not implement TestNG listeners, failure diagnostics, or CI/CD integration yet.
- Do not disable or modify known-defect tests.
- Do not commit or push changes.

Document the task in AI_PROMPTS.md according to the rules defined in AGENTS.md.

Provide a concise implementation summary, including created files, validation results, and any limitations or deviations from the requirements.[@.gitkeep](file:///Users/vadymsula/IdeaProjects/home_assignment/src/test/resources/.gitkeep)

**Outcome:**

Added three serial, package-discovered XML suites using native TestNG group filters and the existing Surefire option.

**Changed Files:**

- `src/test/resources/suites/regression.xml`
- `src/test/resources/suites/known-bugs.xml`
- `src/test/resources/suites/full-suite.xml`
- `AI_PROMPTS.md`

**Validation:**

- `mvn clean test-compile` passed on JDK 17.
- Surefire 3.5.5 with TestNG 7.12.0 parsed and discovered 22 regression, 1 known-bug, and 23 full-suite invocations in native dry runs. An explicit `-Dtest=MainTest#testMainScreenContent` selected one method despite a suite XML argument.
- The live known-bug-only run reached BUG-004 and failed at the existing Paywall visibility wait after rotation. The filtered setup created a driver and initialized the pages. Teardown restored portrait before successfully terminating the session.
- Regression and full-suite validation used dry runs, with live runtime validation deferred.

**Human Review & Comments:**

---
