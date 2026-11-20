package com.zipo.interviewproject.automation.ai;

import com.openai.errors.OpenAIInvalidDataException;
import com.openai.errors.OpenAIIoException;
import com.openai.errors.OpenAIServiceException;
import com.zipo.interviewproject.automation.ai.AiClient.Analysis;
import com.zipo.interviewproject.automation.ai.AiClient.Classification;
import com.zipo.interviewproject.automation.diagnostics.FailureDiagnostics;
import com.zipo.interviewproject.automation.diagnostics.FailureDiagnostics.HealthEvidence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/** Prepares advisory failure analysis for the report without modifying the test result. */
public final class AiFailureAnalyzer {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(AiFailureAnalyzer.class);

    private static final int MAX_ANALYSIS_CHARS = 8_000;

    private static final String UNAVAILABLE_PREFIX =
            "AI failure analysis unavailable. ";

    private final AiConfig config = AiConfig.fromEnvironment();

    private AiClient client;

    public Optional<String> analyze(
            ITestResult result, Optional<Path> directory) {

        if (!config.requested() || !failed(result)) {
            return Optional.empty();
        }

        Optional<String> configurationError = config.validationError();

        if (configurationError.isPresent()) {
            return unavailable(configurationError.get());
        }

        Optional<HealthEvidence> health =
                FailureDiagnostics.healthEvidence(result);

        if (result.getThrowable() == null
                && directory.isEmpty()
                && health.filter(value ->
                value.crashDetected() || value.anrDetected()).isEmpty()) {

            return Optional.of("""
                    Classification: UNKNOWN

                    The invocation failed with insufficient diagnostic evidence for analysis.
                    """);
        }

        try {
            AiEvidence evidence = new AiEvidence(config.apiKey());
            String input = evidence.prepare(result, directory, health);

            if (client == null) {
                client = new AiClient(config);
            }

            Optional<Analysis> analysis = client.analyze(input);

            if (analysis.isEmpty()) {
                return unavailable(
                        "OpenAI returned an incomplete or refused analysis.");
            }

            Analysis response = analysis.get();

            if (!agreesWithHealth(response.classification(), health)) {
                return unavailable(
                        "The model's classification conflicted with deterministic health evidence.");
            }

            return Optional.of(AiEvidence.clip(
                    evidence.redact(format(response)),
                    MAX_ANALYSIS_CHARS));

        } catch (OpenAIServiceException exception) {
            return unavailable(switch (exception.statusCode()) {
                case 401, 403 ->
                        "OpenAI rejected the credentials or access to the model.";
                case 429 ->
                        "OpenAI rate-limited the request.";
                default ->
                        "OpenAI returned HTTP " + exception.statusCode() + ".";
            });

        } catch (OpenAIIoException exception) {
            return unavailable(
                    exception.getCause() instanceof InterruptedIOException
                            ? "The OpenAI request timed out or was interrupted."
                            : "The OpenAI request could not be completed.");

        } catch (OpenAIInvalidDataException exception) {
            return unavailable(
                    "OpenAI returned an invalid structured response.");

        } catch (IOException | RuntimeException exception) {
            return unavailable(
                    "Analysis could not be prepared ("
                            + exception.getClass().getSimpleName() + ").");
        }
    }

    public void close() {
        if (client != null) {
            try {
                client.close();
            } catch (RuntimeException exception) {
                LOGGER.warn("Could not close the OpenAI client ({})",
                        exception.getClass().getSimpleName());
            }
        }
    }

    private static boolean failed(ITestResult result) {
        return result.getStatus() == ITestResult.FAILURE
                || result.getStatus() == ITestResult.SUCCESS_PERCENTAGE_FAILURE;
    }

    private static boolean agreesWithHealth(
            Classification classification,
            Optional<HealthEvidence> health) {

        boolean crash = health
                .map(HealthEvidence::crashDetected)
                .orElse(false);

        boolean anr = health
                .map(HealthEvidence::anrDetected)
                .orElse(false);

        return switch (classification) {
            case APPLICATION_CRASH -> crash;
            case APPLICATION_ANR -> anr;
            default -> !crash && !anr;
        };
    }

    private static String format(Analysis analysis) {
        return """
                Classification: %s

                Failure Summary:
                %s

                Likely Root Cause (hypothesis):
                %s

                Supporting Evidence:
                %s

                Recommended Investigation:
                %s

                Limitations:
                %s
                """.formatted(
                analysis.classification(),
                analysis.failureSummary(),
                analysis.likelyRootCause(),
                bullets(analysis.supportingEvidence()),
                bullets(analysis.recommendedInvestigation()),
                analysis.limitations());
    }

    private static String bullets(List<String> entries) {
        return String.join("\n", entries.stream()
                .map(entry -> "- " + entry)
                .toList());
    }

    private static Optional<String> unavailable(String reason) {
        String message = UNAVAILABLE_PREFIX + reason;

        LOGGER.warn("{}", message);

        return Optional.of(message);
    }
}