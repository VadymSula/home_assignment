package com.zipo.interviewproject.automation.reporting;

import com.zipo.interviewproject.automation.ai.AiFailureAnalyzer;
import com.zipo.interviewproject.automation.diagnostics.DiagnosticArtifacts;
import com.zipo.interviewproject.automation.diagnostics.FailureDiagnostics;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureExternalKey;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.AttachmentOptions;
import io.qameta.allure.util.ResultsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/** Bridges collected framework diagnostics to the native Allure test result. */
public final class AllureReporter {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(AllureReporter.class);

    private static final String TEST_KEY_ATTRIBUTE =
            AllureReporter.class.getName() + ".testKey";

    private static final String SCREENSHOT_ATTACHMENT = "Screenshot";
    private static final String LOGCAT_ATTACHMENT = "Device Logcat";
    private static final String PAGE_SOURCE_ATTACHMENT = "Page Source";
    private static final String STACK_TRACE_ATTACHMENT = "Stack Trace";
    private static final String AI_ANALYSIS_ATTACHMENT = "AI Failure Analysis";

    private static final String IMAGE_PNG = "image/png";
    private static final String TEXT_PLAIN = "text/plain";
    private static final String APPLICATION_XML = "application/xml";

    private static final String PNG_EXTENSION = "png";
    private static final String TXT_EXTENSION = "txt";
    private static final String XML_EXTENSION = "xml";

    private final AiFailureAnalyzer analyzer = new AiFailureAnalyzer();

    public void bindTest(ITestResult result) {
        try {
            AllureLifecycle lifecycle = Allure.getLifecycle();

            lifecycle.getCurrentRootKey().ifPresent(key -> {
                result.setAttribute(TEST_KEY_ATTRIBUTE, key);

                lifecycle.updateTest(key, test -> {
                    for (String group : result.getMethod().getGroups()) {
                        test.getLabels().add(ResultsUtils.createTagLabel(group));
                    }
                });
            });

        } catch (RuntimeException exception) {
            LOGGER.warn("Could not bind the Allure test ({})",
                    exception.getClass().getSimpleName());
        }
    }

    public void completeTest(ITestResult result) {
        if (!(result.removeAttribute(TEST_KEY_ATTRIBUTE)
                instanceof AllureExternalKey key)) {
            return;
        }

        Optional<Path> directory =
                FailureDiagnostics.artifactDirectory(result);

        directory.ifPresent(path -> {
            attachFile(key, path.resolve(DiagnosticArtifacts.SCREENSHOT),
                    SCREENSHOT_ATTACHMENT, IMAGE_PNG, PNG_EXTENSION);

            attachFile(key, path.resolve(DiagnosticArtifacts.LOGCAT),
                    LOGCAT_ATTACHMENT, TEXT_PLAIN, TXT_EXTENSION);

            attachFile(key, path.resolve(DiagnosticArtifacts.PAGE_SOURCE),
                    PAGE_SOURCE_ATTACHMENT, APPLICATION_XML, XML_EXTENSION);

            attachFile(key, path.resolve(DiagnosticArtifacts.STACK_TRACE),
                    STACK_TRACE_ATTACHMENT, TEXT_PLAIN, TXT_EXTENSION);
        });

        try {
            analyzer.analyze(result, directory)
                    .ifPresent(text -> attachAnalysis(key, text));

        } catch (RuntimeException exception) {
            LOGGER.warn("AI failure analysis could not be completed ({})",
                    exception.getClass().getSimpleName());
        }
    }

    public void close() {
        analyzer.close();
    }

    private static void attachFile(
            AllureExternalKey key,
            Path file,
            String name,
            String type,
            String extension) {

        try {
            if (Files.isRegularFile(file)) {
                try (InputStream stream = Files.newInputStream(file)) {
                    Allure.getLifecycle().addAttachment(
                            key, name, type, stream,
                            AttachmentOptions.withFileExtension(extension));
                }
            }
        } catch (IOException | RuntimeException exception) {
            LOGGER.warn("Could not attach {} to Allure ({})",
                    name, exception.getClass().getSimpleName());
        }
    }

    private static void attachAnalysis(AllureExternalKey key, String text) {
        try {
            InputStream stream = new ByteArrayInputStream(
                    text.getBytes(StandardCharsets.UTF_8));

            Allure.getLifecycle().addAttachment(
                    key, AI_ANALYSIS_ATTACHMENT, TEXT_PLAIN, stream,
                    AttachmentOptions.withFileExtension(TXT_EXTENSION));

        } catch (RuntimeException exception) {
            LOGGER.warn("Could not attach AI failure analysis to Allure ({})",
                    exception.getClass().getSimpleName());
        }
    }
}