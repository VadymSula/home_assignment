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