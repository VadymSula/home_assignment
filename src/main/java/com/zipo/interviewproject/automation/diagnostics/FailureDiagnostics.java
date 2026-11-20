package com.zipo.interviewproject.automation.diagnostics;

import com.zipo.interviewproject.automation.config.FrameworkConfig;
import com.zipo.interviewproject.automation.driver.DriverFactory;
import io.appium.java_client.android.AndroidDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.SkipException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/** Coordinates health monitoring and artifact collection for a single TestNG invocation. */
public final class FailureDiagnostics {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(FailureDiagnostics.class);

    private static final String ATTRIBUTE =
            FailureDiagnostics.class.getName();

    private static final String ARTIFACT_DIRECTORY_ATTRIBUTE =
            ATTRIBUTE + ".artifactDirectory";

    private static final Path ARTIFACT_ROOT =
            Path.of("target", "artifacts");

    private static final String MONITORING_FAILURE_MESSAGE =
            "Application health monitoring failed";

    private static final String SAFE_NAME_PATTERN =
            "[^a-zA-Z0-9_-]";

    private final LogcatCapture logcat;
    private final CrashDetector crashDetector;

    private IllegalStateException startFailure;
    private boolean completed;
    private HealthEvidence healthEvidence;

    private FailureDiagnostics(FrameworkConfig config) {
        logcat = new LogcatCapture(
                config.deviceUdid().orElse(null));

        crashDetector = new CrashDetector(config.appPackage());
    }

    public static String begin(
            FrameworkConfig config, ITestResult result) {

        FailureDiagnostics diagnostics =
                new FailureDiagnostics(config);

        // TestNG transfers attributes from the native
        // @BeforeMethod result to the test result.
        result.setAttribute(ATTRIBUTE, diagnostics);

        LOGGER.debug(
                "Initializing diagnostic context for {}",
                result.getMethod().getQualifiedName());

        try {
            diagnostics.logcat.start();

        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();

            diagnostics.startFailure = monitoringFailure(exception);

            throw diagnostics.startFailure;

        } catch (IOException | RuntimeException exception) {
            diagnostics.startFailure = monitoringFailure(exception);

            if (diagnostics.logcat.deviceSerial() == null) {
                throw diagnostics.startFailure;
            }
        }

        return diagnostics.logcat.deviceSerial();
    }

    public static void finish(ITestResult result) {
        if (result.getAttribute(ATTRIBUTE)
                instanceof FailureDiagnostics diagnostics
                && !diagnostics.completed) {

            diagnostics.completed = true;
            diagnostics.evaluate(result);
        }
    }

    public static void release(ITestResult result) {
        if (result.removeAttribute(ATTRIBUTE) != null) {
            LOGGER.debug(
                    "Released diagnostic context for {}",
                    result.getMethod().getQualifiedName());
        }
    }

    public static Optional<Path> artifactDirectory(ITestResult result) {
        Object attribute =
                result.getAttribute(ARTIFACT_DIRECTORY_ATTRIBUTE);

        return attribute instanceof Path path
                ? Optional.of(path)
                : Optional.empty();
    }

    public static Optional<HealthEvidence> healthEvidence(ITestResult result) {
        return result.getAttribute(ATTRIBUTE) instanceof FailureDiagnostics diagnostics
                ? Optional.ofNullable(diagnostics.healthEvidence)
                : Optional.empty();
    }

    public record HealthEvidence(
            boolean crashDetected,
            boolean anrDetected,
            String summary) {
    }

    private void evaluate(ITestResult result) {
        AndroidDriver driver =
                DriverFactory.getActiveDriver().orElse(null);

        boolean failed = isFailed(result) || isSetupFailure(result);

        Path directory = failed
                ? prepareFailureArtifacts(result, driver)
                : null;

        List<String> logs = null;
        Throwable healthIssue = startFailure;

        if (healthIssue == null) {
            try {
                logs = logcat.collect();

                List<CrashDetector.Finding> findings =
                        crashDetector.detect(logs);

                String summary = findings.stream()
                        .map(CrashDetector.Finding::description)
                        .collect(Collectors.joining(", "));

                healthEvidence = new HealthEvidence(
                        findings.stream().anyMatch(
                                finding -> finding.kind() == CrashDetector.Kind.CRASH),
                        findings.stream().anyMatch(
                                finding -> finding.kind() == CrashDetector.Kind.ANR),
                        summary);

                if (!findings.isEmpty()) {
                    LOGGER.error(
                            "Application health check for {} detected {}",
                            result.getMethod().getQualifiedName(),
                            summary);

                    healthIssue = new AssertionError(
                            "Application health check detected " + summary);
                }

            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();

                healthIssue = monitoringFailure(exception);

            } catch (IOException | RuntimeException exception) {
                healthIssue = monitoringFailure(exception);
            }
        }

        if (healthIssue != null) {
            if (result.isSuccess()) {
                result.setThrowable(healthIssue);
                result.setStatus(ITestResult.FAILURE);

                directory = prepareFailureArtifacts(result, driver);

            } else if (failed && result.getThrowable() != healthIssue) {
                Throwable original = result.getThrowable();

                if (original != null) {
                    original.addSuppressed(healthIssue);
                } else {
                    result.setThrowable(healthIssue);
                }
            }
        }

        if (directory != null) {
            if (logs != null) {
                writeLogcat(directory, logs);
            }

            writeTextArtifact(
                    directory,
                    DiagnosticArtifacts.STACK_TRACE,
                    () -> stackTrace(result.getThrowable()));

            if (driver != null) {
                writeTextArtifact(
                        directory,
                        DiagnosticArtifacts.PAGE_SOURCE,
                        driver::getPageSource);
            }
        }
    }

    private static boolean isFailed(ITestResult result) {
        return result.getStatus() == ITestResult.FAILURE
                || result.getStatus()
                == ITestResult.SUCCESS_PERCENTAGE_FAILURE;
    }

    private static boolean isSetupFailure(ITestResult result) {
        return result.getStatus() == ITestResult.SKIP
                && result.getThrowable() != null
                && !(result.getThrowable() instanceof SkipException)
                && result.getSkipCausedBy().stream()
                .anyMatch(ITestNGMethod::isBeforeMethodConfiguration);
    }

    private static Path prepareFailureArtifacts(
            ITestResult result, AndroidDriver driver) {

        Path directory;

        try {
            String testClass = safeName(
                    result.getTestClass().getRealClass().getSimpleName());

            String method = safeName(
                    result.getMethod().getMethodName());

            Path parent = Files.createDirectories(
                    ARTIFACT_ROOT.resolve(testClass));

            directory = Files.createTempDirectory(
                    parent, method + "_");

            result.setAttribute(
                    ARTIFACT_DIRECTORY_ATTRIBUTE, directory);

        } catch (IOException | RuntimeException exception) {
            LOGGER.warn(
                    "Could not create the diagnostic artifact directory ({})",
                    exception.getClass().getSimpleName());

            return null;
        }

        LOGGER.info("Saving failure artifacts to {}", directory);

        if (driver == null) {
            LOGGER.warn(
                    "The Android driver session is unavailable "
                            + "for screenshot and page source capture");
        } else {
            try {
                ScreenshotCapture.capture(
                        driver, directory.resolve(DiagnosticArtifacts.SCREENSHOT));

            } catch (IOException | RuntimeException exception) {
                LOGGER.warn(
                        "Could not capture {} ({})",
                        DiagnosticArtifacts.SCREENSHOT,
                        exception.getClass().getSimpleName());
            }
        }

        return directory;
    }

    private static void writeLogcat(
            Path directory, List<String> logs) {

        try {
            Files.write(directory.resolve(DiagnosticArtifacts.LOGCAT), logs);

        } catch (IOException | RuntimeException exception) {
            LOGGER.warn(
                    "Could not write {} ({})",
                    DiagnosticArtifacts.LOGCAT,
                    exception.getClass().getSimpleName());
        }
    }

    private static void writeTextArtifact(
            Path directory,
            String filename,
            Supplier<String> contents) {

        try {
            Files.writeString(
                    directory.resolve(filename), contents.get());

        } catch (IOException | RuntimeException exception) {
            LOGGER.warn(
                    "Could not capture {} ({})",
                    filename,
                    exception.getClass().getSimpleName());
        }
    }

    private static String stackTrace(Throwable failure) {
        if (failure == null) {
            return "TestNG reported a failure without an exception.";
        }

        StringWriter text = new StringWriter();

        try (PrintWriter writer = new PrintWriter(text)) {
            failure.printStackTrace(writer);
        }

        return text.toString();
    }

    private static IllegalStateException monitoringFailure(
            Exception cause) {

        String detail = cause instanceof IOException
                && cause.getMessage() != null
                ? cause.getMessage()
                : cause.getClass().getSimpleName();

        LOGGER.warn(
                "{} ({})", MONITORING_FAILURE_MESSAGE, detail);

        return new IllegalStateException(
                MONITORING_FAILURE_MESSAGE + ": " + detail,
                cause);
    }

    private static String safeName(String name) {
        return name.replaceAll(SAFE_NAME_PATTERN, "_");
    }
}