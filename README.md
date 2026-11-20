# Android Mobile Test Automation — Home Assignment

A Java-based Android test automation framework for the Rounds Mobile QA Automation Engineer home assignment. It covers the main user journey — **Splash Screen → Main Screen → Download → Paywall** — with independent tests, automated failure diagnostics, optional AI-powered failure analysis, and multi-device CI execution.

## 1. Quick Start — GitHub Actions

The easiest way to explore the framework is through [GitHub Actions — Android Tests](https://github.com/VadymSula/home_assignment/actions/workflows/android-tests.yml). No local Android or Appium setup is required to inspect CI results.

1. Open **Actions → Android Tests → Run workflow**.
2. Select the `main` branch and the desired suite (`regression`, `known-bugs`, or `full-suite`).
3. Optionally enable **OpenRouter analysis for a manual run on main**.
4. Click **Run workflow**. The suite runs on two independent Android emulators.
5. Open the completed workflow run and download the device-specific reports from **Artifacts**.

Manual execution requires repository write access. Reviewers without write access can inspect existing runs and download available artifacts when signed in to GitHub. AI analysis is disabled by default and is not required for test execution.

To view a downloaded Allure report, extract the archive, open a terminal in the `allure-report` directory (the directory containing `index.html`), and run:

```bash
python3 -m http.server 8080 --bind 127.0.0.1
```

Open [http://localhost:8080](http://localhost:8080). This avoids browser restrictions when opening the HTML report directly from the filesystem.

> The default regression suite excludes a known application defect. The `known-bugs` and `full-suite` runs are expected to fail while BUG-004 remains reproducible. This is intentional; the actual failure is preserved in CI and Allure.

The repository is public, so anyone can review the source code, workflow execution history, and available test reports.

If you would like to trigger the workflow directly in this repository, please contact me with your GitHub username, and I will grant collaborator access.

Alternatively, you can fork the repository and run the workflow independently. No API key is required when AI analysis is disabled.

## 2. Scope & Test Strategy

Testing is based on the supplied, unmodified APK (`apps/app-debug.apk`, package `com.zipo.interviewproject`). The test plan was written and committed before test implementation. See [TEST_PLAN.md](TEST_PLAN.md) for the risk assessment, priorities, coverage criteria, and manual/exploratory scope.

| Area | Automated coverage | Executions |
| --- | --- | ---: |
| Splash Screen | Application title, loading indicator, transition to Main Screen | 2 |
| Main Screen | Toolbar, item count, titles, order, uniqueness, Download button availability | 3 |
| Paywall | Opening and verifying paywall content for each of the 11 Earth items | 11 |
| Billing actions | Subscribe, Purchase, Restore Purchases: expected Toast, dismissal, return to Main Screen | 3 |
| Dismissal | Not Now, Android Back, outside tap, and reopening | 3 |
| Orientation | Paywall state preservation after portrait-to-landscape rotation (BUG-004) | 1 |
| **Total** | **3 test classes, 9 test methods** | **23** |

The priority is reliable access to content, navigation, and the paywall. DataProviders cover all 11 Earth items and multiple actions without duplicating test logic. Exhaustive combinations of actions, devices, and configurations are not necessary to demonstrate meaningful coverage for this APK.

### Google Play & Billing

Testing uses Google Play-enabled emulator images without a signed-in Google Play account. Billing is intentionally disabled in the supplied APK: the paywall displays test content, and billing actions are no-ops that produce specific Toast messages. No real purchases are attempted. Missing prices in this state are not treated as a defect.

On a signed-in device with a billing-enabled build, I would additionally verify product loading, localized prices and currencies, subscription offers, purchase cancellation and error/pending states, restore purchases, and entitlement persistence. I would use Google Play Billing test accounts and test payment methods rather than real transactions. These flows cannot be validated against the current no-op APK.

Offline behavior was checked during exploratory testing: the main flow and test paywall remained accessible. Automated network scenarios are outside the current scope.

## 3. Technology Stack & Framework Design

| Technology | Version / purpose |
| --- | --- |
| Java | 17 |
| Maven | 3.9.1 (local), dependency management and test execution |
| Node.js | 22.23.5 |
| Appium / UiAutomator2 | 3.9.1 / 8.6.0 |
| Appium Java Client / Selenium | 10.1.1 / 4.44.0 |
| TestNG / Maven Surefire | 7.12.0 / 3.5.5 |
| SLF4J / Logback | 2.0.19 / 1.6.8 |
| Allure TestNG / Allure Report | 3.0.1 / 2.39.0 |
| OpenAI Java SDK | 4.110.0, optional AI analysis via the Responses API |
| Android SDK / ADB | Emulator management, device logs, application health monitoring |
| GitHub Actions | CI and device matrix execution |

**Why Appium?** It supports black-box testing of an Android APK without source code or build modifications, including system gestures, Toast messages, orientation changes, and application lifecycle actions. Java, Appium, and TestNG offer fine-grained control over test lifecycle, diagnostics, data-driven tests, and AI integration. Maestro would be a reasonable alternative for simpler smoke flows; Espresso is more attractive when an instrumented application build is available. Appium also provides a potential path to iOS automation.

The framework uses the Page Object Model, composition, centralized configuration, explicit waits, reusable actions, and TestNG DataProviders. Application-specific expectations belong to the test layer and are not derived from the actual UI. Native APIs and stable resource IDs are preferred. Functional tests do not use fixed sleeps or automatic retries to hide failures.

```text
apps/                         # APK under test
src/main/java/com/zipo/interviewproject/automation/
├── base/                     # Test lifecycle
├── config/                   # Framework configuration
├── driver/                   # DriverFactory / session management
├── pages/                    # Page Objects
├── elements/                 # Element and Toast actions
├── waits/                    # Explicit waits
├── gestures/                 # Scroll and tap actions
├── device/                   # Relaunch and orientation management
├── diagnostics/              # Screenshot, logcat, crash/ANR detection
├── listeners/                # TestNG lifecycle integration
├── reporting/                # Allure attachments
└── ai/                       # Optional AI failure analyzer
src/main/resources/           # Framework, logging, Allure and AI prompt settings
src/test/java/com/zipo/interviewproject/automation/
├── tests/                    # SplashTest, MainTest, PaywallTest
└── data/                     # TestData, TestDataProvider
src/test/resources/suites/    # TestNG suite XML files
.github/workflows/            # Android CI workflow
```

Each test invocation has an independent Appium session. This costs additional execution time but provides isolation, reproducibility, and individual test execution. Failed-state diagnostics are collected before orientation restoration and session teardown.

Two reliability improvements were made during validation:

- **Splash Screen:** a single UI hierarchy snapshot captures the title and loading indicator in the same screen state after a controlled relaunch, reducing the race condition caused by separate Appium commands. A very short-lived splash screen can still be missed by the automation driver.
- **RecyclerView:** the initial implementation assumed all 11 items fit in the viewport. The first CI run exposed this limitation on Pixel 4a. Scroll-aware traversal now collects rows across overlapping viewports, preserving order, and the Download button checks scroll to the relevant item. The approach is tailored to this list structure; identical repeated sequences may make viewport merging ambiguous.

## 4. Local Execution & Test Suites

### One-time setup

Install JDK 17, Maven, Node.js, Android Studio/SDK, and ensure `adb` is available in `PATH`. Create a Google Play-enabled emulator, preferably Pixel 7 with Android 14 (API 34), or Pixel 4a with Android 16 (API 36). Use a compatible ARM64 system image on Apple Silicon; CI uses x86_64.

```bash
npm install -g appium@3.9.1
appium driver install uiautomator2@8.6.0
```

Start the emulator, wait for Android to finish booting, unlock the screen, and verify `adb devices` reports an online device. Start Appium in a separate terminal:

```bash
appium --address 127.0.0.1 --port 4723
```

Clone the repository:

```bash
git clone https://github.com/VadymSula/home_assignment.git
cd home_assignment
```

### One-command regression run

After the one-time setup:

```bash
mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/suites/regression.xml
```

### Test suites & execution strategy

| Suite | Executions per device | Purpose |
| --- | ---: | --- |
| `regression` | 22 | Regular functional coverage; excludes the `known-bug` group |
| `known-bugs` | 1 | Reproduces BUG-004: paywall closes on rotation |
| `full-suite` | 23 | All automated scenarios, including BUG-004 |

The workflow runs `regression` by default on pushes to `main` and pull requests. The known defect is isolated so it does not block every regular CI run. Its test is neither disabled nor artificially forced to pass or fail. `known-bugs` and `full-suite` retain a non-zero exit code when the defect is reproduced. Once the application defect is fixed, its test should be included in regular regression.

The known-bugs suite is also a convenient way to demonstrate failure artifacts and optional AI analysis without running the entire suite.

```bash
# Reproduce the known defect
mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/suites/known-bugs.xml

# Run every scenario
mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/suites/full-suite.xml

# Run an individual test
mvn test -Dtest=MainTest#testMainScreenContent
```

### Configuration

Defaults are in `src/main/resources/framework.properties`; JVM system properties take precedence. No Java code changes are needed to select a different device or APK configuration.

| Property | Default |
| --- | --- |
| `appium.server.url` | `http://127.0.0.1:4723` |
| `device.name` | `Google Pixel 7` |
| `device.udid` | Empty; auto-selection requires exactly one connected device |
| `platform.name` | `Android` |
| `platform.version` | `14` |
| `automation.name` | `UiAutomator2` |
| `app.apk.path` | `apps/app-debug.apk` |
| `app.package` | `com.zipo.interviewproject` |
| `app.activity` | Empty |
| `wait.timeout.seconds` | `10` |
| `uiautomator2.system.port` | Empty |

Example for a different running emulator:

```bash
mvn clean test \
  -Dsurefire.suiteXmlFiles=src/test/resources/suites/regression.xml \
  -Ddevice.name="Google Pixel 4a" \
  -Ddevice.udid=emulator-5554 \
  -Dplatform.version=16
```

Replace the UDID with the actual serial from `adb devices`. Appium and ADB must target the same device.

## 5. Failure Diagnostics & Reporting

Logcat monitoring starts before Appium session creation and uses device-side invocation boundaries. The framework reads the `main`, `system`, `crash`, and `events` buffers without clearing shared device logs. `CrashDetector` identifies Java crashes, corroborated native crashes, and ANRs associated with the configured application package and its subprocesses.

An otherwise passing test fails if a crash or ANR is detected, or if health monitoring cannot reliably complete. For an existing failure, the original exception is preserved and additional health findings are attached as suppressed exceptions. A timeout alone is not treated as a confirmed ANR.

For each failed invocation, available artifacts are collected independently:

```text
target/artifacts/<TestClass>/<testMethod>_<execution-id>/
├── screenshot.png
├── logcat.txt
├── page-source.xml
└── stacktrace.txt
```

Screenshots and page source require a reachable Appium session. If that session is lost, the framework still attempts to collect the remaining artifacts. Android's circular log buffers and device availability impose limits on crash/ANR detection.

Allure provides features, meaningful test steps, parameters, test status, and failure attachments. Passing tests do not generate unnecessary failure artifacts. After a successful **or failed** Maven test run, execute separately:

```bash
mvn allure:report
```

The HTML report is generated at `target/allure-report/index.html`. To view it locally:

```bash
mvn allure:serve
```

Raw Allure results are stored in `target/allure-results/` and Surefire reports in `target/surefire-reports/`. Running `mvn clean` removes previous local results and artifacts under `target`.

## 6. AI-Powered Failure Analysis

AI is used within the test execution workflow, not just during development. The optional `AiFailureAnalyzer` adds an **AI Failure Analysis** attachment to the corresponding failed test in Allure.

```text
Failed test → deterministic health monitoring → diagnostic artifacts
            → selected and redacted evidence → LLM Structured Output
            → validated analysis → Allure attachment
```

The model receives bounded excerpts of the exception, stack trace, relevant Logcat records, test metadata, and structural UI hierarchy attributes. Raw screenshots, complete diagnostic files, and the API key are not included in the model's input evidence. The structured response contains a classification, failure summary, likely root cause, supporting evidence, recommended investigation, and limitations.

**Why failure analysis?** At scale, investigating failing tests is a major maintenance cost. AI can help distinguish likely application bugs from locator, synchronization, assertion, and environment issues. It remains advisory: an `UNKNOWN` classification is valid, hypotheses are not confirmed root causes, and deterministic crash/ANR findings take precedence. AI never changes test outcomes, assertions, locators, or application code.

### Enable AI locally (optional)

AI is disabled by default and requires no credentials for normal test execution. The live integration was successfully validated with OpenRouter and `inclusionai/ling-3.2-flash:free`, using the OpenAI Java SDK and Responses API. A specific model was selected after the automatic free-model router repeatedly exhausted the output token budget.

In Bash or Zsh:

```bash
export AI_ANALYSIS_ENABLED=true
export OPENAI_BASE_URL=https://openrouter.ai/api/v1
export OPENAI_MODEL=inclusionai/ling-3.2-flash:free
printf 'OpenRouter API key: '
read -rs OPENAI_API_KEY
export OPENAI_API_KEY
printf '\n'

mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/suites/known-bugs.xml
```

After the expected test failure, run `mvn allure:serve` and expand the **AI Failure Analysis** attachment. To disable AI again:

```bash
unset AI_ANALYSIS_ENABLED OPENAI_API_KEY OPENAI_BASE_URL OPENAI_MODEL
```

Runtime prompt: [failure-analysis-prompt.txt](src/main/resources/ai/failure-analysis-prompt.txt). Requests are bounded to 12,000 characters of evidence, 2,000 output tokens, a 45-second request timeout, and zero SDK retries. Analysis can increase the duration of a failed invocation before teardown.

### Live example & provider limitations

The following is a genuine Allure result from a local Android 14 execution with a successful external AI response. The analysis identified a possible paywall state-restoration issue following an activity relaunch and explicitly stated the limitations of the evidence.

![Successful AI Failure Analysis for BUG-004 in Allure Report](docs/images/ai-failure-analysis.png)

**Free-model availability is not guaranteed.** During the manual AI-enabled CI execution, both device jobs received HTTP 429. A separate direct API request confirmed that the upstream provider's shared rate-limit pool was temporarily exhausted (`upstream_provider_shared_pool`). This is an external provider limitation, not a test execution failure caused by the AI integration. A successful AI response was verified locally; a successful live AI response in CI has not yet been demonstrated.

The framework handles provider errors, rate limits, timeouts, and invalid responses gracefully: the original test result and diagnostic artifacts are preserved, and Allure records why AI analysis is unavailable. The deterministic automation remains fully usable without AI. No paid service or additional provider is required to run the tests.

### Privacy & security

AI is opt-in because redaction cannot guarantee removal of all sensitive data. The request uses `store(false)`, but the external provider's own processing and retention policies still apply. Only enable AI with authorized test data. Raw diagnostic files may contain sensitive information and should be reviewed before publication. API credentials must never be committed or included in reports.

## 7. CI/CD — Multi-Device Execution

The [Android Tests workflow](.github/workflows/android-tests.yml) uses GitHub Actions, Ubuntu runners, and hardware-accelerated Google Play emulators.

| Device | Android | API | System image |
| --- | --- | ---: | --- |
| Google Pixel 7 | 14 | 34 | Google Play, x86_64 |
| Google Pixel 4a | 16 | 36 | Google Play, x86_64 |

Matrix jobs run independently and in parallel on separate runners. Tests remain sequential within each job. `fail-fast: false` ensures that a failure on one device does not cancel the other. Dependencies are pinned, Maven dependencies are cached, and device/Appium readiness is checked before executing the suite.

Each device job uploads its available Allure HTML report, raw Allure results, failure diagnostics, Surefire reports, and Appium server log, including on failure. Artifacts have device-specific names and a seven-day retention period. Report generation and artifact collection do not suppress the original Maven failure status.

AI is enabled only for an explicitly requested manual run on `main`. To configure it in a repository you maintain, create the `android-ai` GitHub environment, restrict deployment branches to `main`, and add the environment secret `OPENAI_API_KEY` containing an OpenRouter key. Keep this secret separate from the regular `android-ci` environment. Normal regression requires no AI credentials. Manual runs are optional; reviewers can inspect existing results.

## 8. Bugs & Validation Status

See [BUG_REPORTS.md](BUG_REPORTS.md) for reproduction steps, expected versus actual results, and severity.

| ID | Finding | Severity / status |
| --- | --- | --- |
| BUG-001 | Not Now button is partially clipped in landscape | Medium; manual |
| BUG-002 | Reverse portrait rotation leaves the screen in landscape | Requires verification; may be platform behavior |
| BUG-003 | Download buttons for Earth 10 and Earth 11 are shifted right | Low; manual |
| BUG-004 | Open paywall closes after portrait-to-landscape rotation | Medium; manual and automated reproduction |

BUG-001 is not represented by a basic visibility assertion because a partially clipped button can still be displayed and clickable to Appium. A geometry-based or visual check would be more appropriate.

### Validation performed

- Suite discovery confirmed 22 regression, 1 known-bug, and 23 full-suite invocations.
- An earlier local full-suite run produced 21 passes and two failures: BUG-004 and a transient Splash indicator check. The Splash content test was subsequently stabilized using a single snapshot and passed a focused rerun.
- Local focused testing confirmed Allure reporting and the original BUG-004 failure with all four diagnostic artifacts captured before portrait restoration. Allure maps the Selenium timeout to **Broken**; this is a genuine non-passing result, not a hidden application defect.
- During implementation, synthetic log cases and TestNG lifecycle fixtures exercised crash, ANR, and monitoring-error propagation. Loopback AI mock validation covered structured responses, missing credentials, provider errors, timeouts, redaction, and consistency with health evidence. These checks are not evidence of a real crash or ANR in the supplied APK; they were performed at the corresponding implementation stages, before subsequent manual refinements.
- A live local OpenRouter request produced a structured AI attachment. Manual AI-enabled CI runs reproduced BUG-004 on both emulators, retained diagnostic attachments, and recorded the external HTTP 429 limitation.

### CI results

The first automatic [CI run](https://github.com/VadymSula/home_assignment/actions/runs/44666603807) completed in 8m 12s with both device artifacts uploaded:

| Device | Passed | Failed |
| --- | ---: | ---: |
| Pixel 7 / API 34 | 22 | 0 |
| Pixel 4a / API 36 | 20 | 2 |
| **Total** | **42** | **2** |

Both failures on Pixel 4a were caused by the original viewport-dependent RecyclerView checks. The implementation was updated to support scrolling across different screen sizes.

The [post-fix CI run #3](https://github.com/VadymSula/home_assignment/actions/runs/44671193649) succeeded on both devices in **7m 47s**, with two device-specific artifacts uploaded. Both regression jobs passed (22 configured invocations per device, 44 in total). This validates the multi-device workflow after the RecyclerView fix. The known-bug scenario remains isolated from the default regression suite.

## 9. Scaling to 20 Apps & Multiple Devices

For a portfolio of 20 applications, I would separate a reusable automation core from application-specific modules. The core would include driver/session management, configuration, waits, gestures, diagnostics, reporting, and AI integration. Each application would own its Page Objects, tests, expectations, fixtures, and APK/package/activity configuration.

```text
automation-core/
apps/
  app-a/   # app configuration, pages, tests
  app-b/
  ...
  app-t/
```

Shared onboarding, permissions, or paywall components could be reused where the UI contracts genuinely match, rather than introducing a universal Page Object full of conditional logic. Versioned APKs would come from a build pipeline or artifact repository, rather than Git.

CI would use a configurable `app × device × OS` matrix. Representative smoke checks would run on pull requests; broader regression would run on a schedule or before release. Running the complete combination for every commit would be unnecessarily expensive. A device farm could provide real-device coverage when needed.

Parallel execution on a shared host would require explicit device allocation, unique ADB device serials (UDIDs) and UiAutomator2 system ports, isolated test data and artifacts, and verified session/diagnostic isolation. The current framework is a starting point for that design, not a validated shared-host execution platform. Centralized reporting and failure grouping would make results discoverable by app, build, device, and OS.

## 10. Future Improvements

The following improvements are deliberately outside the time-boxed assignment. I would prioritize reliability and diagnostic confidence before additional infrastructure.

| Improvement | Proposed approach |
| --- | --- |
| Cross-device reliability | Validate more screen sizes, densities, real devices, and OS versions; strengthen list traversal with stable row identifiers where available. |
| Diagnostic validation | Retain dedicated unit/integration tests for crash/ANR parsing, Logcat boundaries, lifecycle behavior, and AI response validation; add controlled on-device crash/ANR fixtures. |
| Network testing | Automate offline, slow/unstable connectivity, reconnect, and recovery; use emulator network controls or a controlled proxy for applications with actual backend traffic. |
| Healenium / locator self-healing | Evaluate native Appium compatibility and use healing only with confidence thresholds, recorded evidence, and review. Never silently bypass real UI regressions or assertions. |
| AI Test Maintenance Agent | Analyze failures, UI hierarchies, execution history, and build changes to propose Page Object or locator updates in a separate branch or PR, with mandatory human review and revalidation. |
| Reporting and AI at scale | Build Allure history/trends, failure fingerprinting, centralized reporting, and asynchronous post-run triage. Add bounded retries with exponential backoff, configurable fallback models, and cost/privacy controls if justified. |
| Visual, accessibility, and billing coverage | Add visual regression for clipping/layout defects, accessibility checks, and signed-in Google Play Billing sandbox scenarios when a billing-enabled build becomes available. |

Healenium and the AI Test Maintenance Agent were intentionally not added to the current implementation. Both introduce additional complexity and false-positive risks that need careful evaluation. A working, evidence-based failure analyzer delivers more immediate value for this assignment without introducing automatic test healing. Device farms, reporting servers, and more complex abstractions are likewise deferred until justified by actual scale.

## 11. AI-Assisted Development & Documentation

Codex was used to implement defined engineering tasks. Test strategy, architecture, expected results, code review, and runtime validation remained under human control. AI-generated code was reviewed and refined rather than accepted without verification.

| Document | Purpose |
| --- | --- |
| [TEST_PLAN.md](TEST_PLAN.md) | Risk-based test strategy and planned coverage, committed before test implementation |
| [BUG_REPORTS.md](BUG_REPORTS.md) | Findings and reproduction steps |
| [AI_PROMPTS.md](AI_PROMPTS.md) | Development prompts, implementation outcomes, validation, and human review |
| [AGENTS.md](AGENTS.md) | Development and AI-assisted workflow conventions |

The framework is intentionally proportionate to the assignment: readable, independent tests, reproducible diagnostics, honest reporting of failures, and practical AI integration without unnecessary production-level complexity.
