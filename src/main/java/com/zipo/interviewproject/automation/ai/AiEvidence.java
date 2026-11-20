package com.zipo.interviewproject.automation.ai;

import com.zipo.interviewproject.automation.config.FrameworkConfig;
import com.zipo.interviewproject.automation.diagnostics.DiagnosticArtifacts;
import com.zipo.interviewproject.automation.diagnostics.FailureDiagnostics.HealthEvidence;
import org.testng.ITestResult;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Selects and redacts diagnostic text before it crosses the provider boundary. */
final class AiEvidence {

    private static final int MAX_INPUT_CHARS = 12_000;
    private static final int MAX_LINE_CHARS = 240;

    private static final int MAX_DESCRIPTION_CHARS = 400;
    private static final int MAX_PLATFORM_FIELD_CHARS = 40;
    private static final int MAX_HEALTH_SUMMARY_CHARS = 500;

    private static final int MAX_TEST_PARAMETERS = 6;
    private static final int MAX_PARAMETER_CHARS = 160;

    private static final int MAX_STACK_TRACE_LINES = 12;
    private static final int MAX_FAILURE_MESSAGE_LINES = 8;
    private static final int MAX_FAILURE_MESSAGE_CHARS = 600;

    private static final int MAX_LOG_LINES_PER_CATEGORY = 12;

    private static final int MAX_XML_BYTES = 64 * 1024;
    private static final int MAX_XML_ROWS = 20;
    private static final int MAX_NODE_TYPE_CHARS = 70;

    private static final String UNAVAILABLE = "Unavailable";
    private static final String REDACTED = "[REDACTED]";

    private static final String SENSITIVE_FIELDS =
            "(?:[\\w-]*[-_])?(?:authorization|api[-_ ]?key|(?:access|refresh|session)?[-_ ]?token"
                    + "|session[-_ ]?id|device[-_ ]?udid|password|passwd|passphrase|(?:client[-_ ]?)?secret|cookies?"
                    + "|account(?:[-_ ]?(?:id|name|number))?|card[-_ ]?number|user[-_ ]?(?:name|id)"
                    + "|email|phone(?:[-_ ]?number)?)";

    private static final Pattern SENSITIVE_NAME =
            Pattern.compile("(?i)" + SENSITIVE_FIELDS);

    private static final Pattern SENSITIVE_LINE = Pattern.compile(
            "(?i)\\b(?:" + SENSITIVE_FIELDS + ")[\"']?(?:\\s*[:=]|\\s+(?:is|was)\\b)");

    private static final Pattern PRIVATE_VALUES = Pattern.compile(
            "(?i)\\b(?:sk-[A-Za-z0-9_-]{8,}|(?:Bearer|Basic)\\s+[^\\s,;]+"
                    + "|eyJ[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+"
                    + "|[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,})"
                    + "|(?:/Users/|/home/|[A-Z]:\\\\Users\\\\)[^/\\\\\\s]+");

    private static final Pattern LOG_RECORD = Pattern.compile(
            "^\\s*\\d+\\.\\d+\\s+\\d+\\s+\\d+\\s+[A-Z]\\s+(\\w+)\\s*:\\s?(.*)$");

    private static final Pattern RESOURCE_ID_PATTERN =
            Pattern.compile("[\\w.:/]{1,150}");

    private static final Set<String> HEALTH_TAGS = Set.of(
            "am_crash",
            "am_anr",
            "AndroidRuntime",
            "DEBUG",
            "libc"
    );

    private static final Set<String> UI_TAGS = Set.of(
            "ActivityManager",
            "ActivityTaskManager",
            "WindowManager"
    );

    private final String apiKey;

    AiEvidence(String apiKey) {
        this.apiKey = apiKey;
    }

    String prepare(
            ITestResult result,
            Optional<Path> directory,
            Optional<HealthEvidence> health) {

        FrameworkConfig config = FrameworkConfig.load();
        String app = config.appPackage();
        Throwable failure = result.getThrowable();

        String stack = directory
                .map(path -> stackExcerpt(path.resolve(DiagnosticArtifacts.STACK_TRACE)))
                .orElse(UNAVAILABLE);

        String logs = directory
                .map(path -> logExcerpt(path.resolve(DiagnosticArtifacts.LOGCAT), app))
                .orElse(UNAVAILABLE);

        String hierarchy = directory
                .map(path -> pageExcerpt(path.resolve(DiagnosticArtifacts.PAGE_SOURCE), app))
                .orElse(UNAVAILABLE);

        String healthSummary = health
                .map(value -> value.summary().isBlank()
                        ? "No crash or ANR detected in the scoped device logs."
                        : value.summary())
                .orElse("Application health evidence unavailable.");

        return clip("""
                Test class: %s
                Test method: %s
                Test description: %s
                Test parameters: %s
                Configured platform: %s %s, %s
                Deterministic application health: %s
                Exception: %s
                Message: %s

                Stack trace excerpt:
                %s

                Application-related device Logcat:
                %s

                UI hierarchy structure (class, resource ID, displayed):
                %s
                """.formatted(
                result.getTestClass().getRealClass().getName(),
                result.getMethod().getMethodName(),
                clip(redact(result.getMethod().getDescription()),
                        MAX_DESCRIPTION_CHARS),
                parameters(result),
                clip(redact(config.platformName()), MAX_PLATFORM_FIELD_CHARS),
                clip(redact(config.platformVersion()), MAX_PLATFORM_FIELD_CHARS),
                clip(redact(config.automationName()), MAX_PLATFORM_FIELD_CHARS),
                clip(redact(healthSummary), MAX_HEALTH_SUMMARY_CHARS),
                failure == null ? UNAVAILABLE : failure.getClass().getName(),
                failure == null ? UNAVAILABLE : failureMessage(failure),
                stack,
                logs,
                hierarchy
        ), MAX_INPUT_CHARS);
    }

    String redact(String text) {
        if (text == null) {
            return "";
        }

        String redacted = text.replace(apiKey, REDACTED);

        return String.join("\n", redacted.lines()
                .map(line -> SENSITIVE_LINE.matcher(line).find()
                        ? REDACTED
                        : PRIVATE_VALUES.matcher(line).replaceAll(REDACTED))
                .toList());
    }

    private String parameters(ITestResult result) {
        Parameter[] names = result.getMethod()
                .getConstructorOrMethod()
                .getMethod()
                .getParameters();

        Object[] values = result.getParameters();
        List<String> selected = new ArrayList<>();

        for (int index = 0;
             index < Math.min(names.length, values.length)
                     && selected.size() < MAX_TEST_PARAMETERS;
             index++) {

            String name = names[index].getName();
            Object value = values[index];

            if (value instanceof String
                    || value instanceof Number
                    || value instanceof Boolean) {

                selected.add(name + "="
                        + (SENSITIVE_NAME.matcher(name).matches()
                        ? REDACTED
                        : clip(redact(value.toString()), MAX_PARAMETER_CHARS)));
            }
        }

        return String.join(", ", selected);
    }

    private String stackExcerpt(Path file) {
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            return String.join("\n", reader.lines()
                    .filter(AiEvidence::technicalTraceLine)
                    .limit(MAX_STACK_TRACE_LINES)
                    .map(line -> clip(redact(line), MAX_LINE_CHARS))
                    .toList());

        } catch (IOException | UncheckedIOException exception) {
            return UNAVAILABLE;
        }
    }

    private String failureMessage(Throwable failure) {
        if (failure.getMessage() == null) {
            return UNAVAILABLE;
        }

        return clip(redact(String.join("\n", failure.getMessage()
                .lines()
                .filter(AiEvidence::technicalTraceLine)
                .limit(MAX_FAILURE_MESSAGE_LINES)
                .toList())), MAX_FAILURE_MESSAGE_CHARS);
    }

    private static boolean technicalTraceLine(String line) {
        String text = line.stripLeading();

        return !text.startsWith("Capabilities {")
                && !text.startsWith("System info:")
                && !text.startsWith("Driver info:")
                && !text.startsWith("Session ID:");
    }

    private String logExcerpt(Path file, String appPackage) {
        Pattern app = Pattern.compile(
                "(?<![\\w.])" + Pattern.quote(appPackage) + "(?![\\w.])");

        Deque<String> health = new ArrayDeque<>();
        Deque<String> activity = new ArrayDeque<>();

        try (BufferedReader reader = Files.newBufferedReader(file)) {
            for (String line; (line = reader.readLine()) != null; ) {
                Matcher record = LOG_RECORD.matcher(line);

                if (!record.matches()
                        || !app.matcher(record.group(2)).find()) {
                    continue;
                }

                String tag = record.group(1);

                if (HEALTH_TAGS.contains(tag)
                        || record.group(2).startsWith("ANR in ")) {

                    retain(health, line);

                } else if (UI_TAGS.contains(tag)) {
                    retain(activity, line);
                }
            }

        } catch (IOException exception) {
            return UNAVAILABLE;
        }

        return String.join("\n", health)
                + "\n" + String.join("\n", activity);
    }

    private void retain(Deque<String> lines, String line) {
        if (lines.size() == MAX_LOG_LINES_PER_CATEGORY) {
            lines.removeFirst();
        }

        lines.addLast(clip(redact(line.strip()), MAX_LINE_CHARS));
    }

    private String pageExcerpt(Path file, String appPackage) {
        Set<String> rows = new LinkedHashSet<>();

        try (InputStream input = Files.newInputStream(file)) {
            XMLInputFactory factory = XMLInputFactory.newFactory();

            factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
            factory.setProperty(
                    XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);

            XMLStreamReader xml = factory.createXMLStreamReader(
                    new ByteArrayInputStream(input.readNBytes(MAX_XML_BYTES)));

            try {
                while (xml.hasNext() && rows.size() < MAX_XML_ROWS) {
                    if (xml.next() == XMLStreamConstants.START_ELEMENT
                            && appPackage.equals(
                            xml.getAttributeValue(null, "package"))) {

                        rows.add(nodeSummary(xml, appPackage));
                    }
                }
            } finally {
                xml.close();
            }

        } catch (IOException | XMLStreamException exception) {
            // A truncated hierarchy can still supply useful structural rows.
        }

        return rows.isEmpty()
                ? UNAVAILABLE
                : redact(String.join("\n", rows));
    }

    private static String nodeSummary(
            XMLStreamReader xml, String appPackage) {

        String type = xml.getLocalName();
        String id = xml.getAttributeValue(null, "resource-id");
        String displayed = xml.getAttributeValue(null, "displayed");

        String safeId = id != null
                && id.startsWith(appPackage + ":id/")
                && RESOURCE_ID_PATTERN.matcher(id).matches()
                ? id
                : "";

        return clip(type, MAX_NODE_TYPE_CHARS)
                + " " + safeId
                + ("true".equals(displayed) ? " displayed" : "");
    }

    static String clip(String text, int limit) {
        return text.length() <= limit
                ? text
                : text.substring(0, limit - 3) + "...";
    }
}