package com.zipo.interviewproject.automation.ai;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.core.LogLevel;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.ResponseStatus;
import com.openai.models.responses.StructuredResponse;
import com.openai.models.responses.StructuredResponseOutputMessage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

/** Owns the OpenAI SDK client and the structured Responses API contract. */
final class AiClient {

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(45);
    private static final int MAX_OUTPUT_TOKENS = 2000;
    private static final String PROMPT_RESOURCE = "/ai/failure-analysis-prompt.txt";

    private final OpenAIClient client;
    private final String model;
    private final String instructions;

    AiClient(AiConfig config) throws IOException {
        model = config.model();
        try (InputStream prompt = AiClient.class.getResourceAsStream(PROMPT_RESOURCE)) {
            if (prompt == null) {
                throw new IOException("Failure-analysis prompt resource is missing.");
            }
            instructions = new String(prompt.readAllBytes(), StandardCharsets.UTF_8);
        }

        OpenAIOkHttpClient.Builder builder = OpenAIOkHttpClient.builder()
                .apiKey(config.apiKey())
                .timeout(REQUEST_TIMEOUT)
                .maxRetries(0)
                .logLevel(LogLevel.OFF)
                .followRedirects(false);
        if (!config.baseUrl().isEmpty()) {
            builder.baseUrl(config.baseUrl());
        }
        client = builder.build();
    }

    Optional<Analysis> analyze(String evidence) {
        StructuredResponse<Analysis> response = client.responses().create(ResponseCreateParams.builder()
                .model(model)
                .instructions(instructions)
                .input(evidence)
                .store(false)
                .maxOutputTokens(MAX_OUTPUT_TOKENS)
                .text(Analysis.class)
                .build());

        if (response.status().filter(ResponseStatus.COMPLETED::equals).isEmpty()
                || response.error().isPresent() || response.incompleteDetails().isPresent()) {
            return Optional.empty();
        }
        List<StructuredResponseOutputMessage.Content<Analysis>> content = response.output().stream()
                .flatMap(item -> item.message().stream())
                .flatMap(message -> message.content().stream())
                .toList();
        if (content.stream().anyMatch(StructuredResponseOutputMessage.Content::isRefusal)) {
            return Optional.empty();
        }
        return content.stream().filter(StructuredResponseOutputMessage.Content::isOutputText)
                .map(StructuredResponseOutputMessage.Content::asOutputText)
                .findFirst().filter(Analysis::isComplete);
    }

    void close() {
        client.close();
    }

    public record Analysis(
            Classification classification, String failureSummary, String likelyRootCause,
            List<String> supportingEvidence, List<String> recommendedInvestigation, String limitations) {

        boolean isComplete() {
            return classification != null && hasText(failureSummary) && hasText(likelyRootCause)
                    && hasText(limitations) && supportingEvidence != null
                    && (classification == Classification.UNKNOWN || !supportingEvidence.isEmpty())
                    && supportingEvidence.stream().allMatch(Analysis::hasText)
                    && recommendedInvestigation != null && !recommendedInvestigation.isEmpty()
                    && recommendedInvestigation.stream().allMatch(Analysis::hasText);
        }

        private static boolean hasText(String value) {
            return value != null && !value.isBlank();
        }
    }

    public enum Classification {
        APPLICATION_BUG,
        LOCATOR_ISSUE,
        SYNCHRONIZATION_ISSUE,
        ASSERTION_FAILURE,
        ENVIRONMENT_ISSUE,
        APPLICATION_CRASH,
        APPLICATION_ANR,
        UNKNOWN
    }
}
