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
