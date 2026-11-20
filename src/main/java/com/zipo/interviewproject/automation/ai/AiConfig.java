package com.zipo.interviewproject.automation.ai;

import java.net.URI;
import java.util.Optional;

/** Holds opt-in AI settings for one TestNG run. */
record AiConfig(String enabled, String apiKey, String model, String baseUrl) {

    private static final String AI_ENABLED_KEY = "AI_ANALYSIS_ENABLED";
    private static final String API_KEY = "OPENAI_API_KEY";
    private static final String MODEL_KEY = "OPENAI_MODEL";
    private static final String BASE_URL_KEY = "OPENAI_BASE_URL";

    private static final String DEFAULT_MODEL = "gpt-4.1-mini";
    private static final String LOOPBACK_HOST = "127.0.0.1";
    private static final String OPENROUTER_BASE_URL = "https://openrouter.ai/api/v1";

    private static final String MODEL_NAME_PATTERN =
            "[A-Za-z0-9][A-Za-z0-9._:/-]*";

    static AiConfig fromEnvironment() {
        String enabled = environment(AI_ENABLED_KEY, "false");

        if (enabled.equalsIgnoreCase("false") || enabled.isEmpty()) {
            return new AiConfig(enabled, "", "", "");
        }

        return new AiConfig(
                enabled,
                environment(API_KEY, ""),
                environment(MODEL_KEY, DEFAULT_MODEL),
                environment(BASE_URL_KEY, "")
        );
    }

    boolean requested() {
        return !enabled.equalsIgnoreCase("false") && !enabled.isEmpty();
    }

    Optional<String> validationError() {
        if (!enabled.equalsIgnoreCase("true")) {
            return Optional.of(AI_ENABLED_KEY + " must be true or false.");
        }

        if (apiKey.isEmpty() || apiKey.chars().anyMatch(Character::isWhitespace)) {
            return Optional.of(API_KEY + " is missing or invalid.");
        }

        if (!model.matches(MODEL_NAME_PATTERN)) {
            return Optional.of(MODEL_KEY + " must be a valid model identifier.");
        }

        if (!baseUrl.isEmpty()
                && !OPENROUTER_BASE_URL.equals(baseUrl)
                && !isLoopbackMock(baseUrl)) {

            return Optional.of(BASE_URL_KEY
                    + " must point to OpenRouter or a loopback mock endpoint.");
        }

        return Optional.empty();
    }

    private static boolean isLoopbackMock(String url) {
        try {
            URI uri = URI.create(url);

            return ("http".equals(uri.getScheme()) || "https".equals(uri.getScheme()))
                    && LOOPBACK_HOST.equals(uri.getHost())
                    && uri.getUserInfo() == null
                    && uri.getQuery() == null
                    && uri.getFragment() == null;

        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private static String environment(String name, String fallback) {
        return System.getenv().getOrDefault(name, fallback).strip();
    }

    @Override
    public String toString() {
        return "AiConfig[requested=" + requested() + "]";
    }
}