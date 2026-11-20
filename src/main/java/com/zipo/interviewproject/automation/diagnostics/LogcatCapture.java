package com.zipo.interviewproject.automation.diagnostics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/** Collects one invocation's device logs independently of the Appium session. */
final class LogcatCapture {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(LogcatCapture.class);

    private static final String MARKER_TAG = "MobileTestDiag";

    private static final Pattern MARKER = Pattern.compile(
            "^\\s*\\d+\\.\\d+\\s+\\d+\\s+\\d+\\s+I\\s+"
                    + MARKER_TAG + ":\\s+(\\S+)$");

    private static final long COMMAND_TIMEOUT_SECONDS = 20;
    private static final long PROCESS_TERMINATION_TIMEOUT_SECONDS = 2;

    private final String id = UUID.randomUUID().toString();

    private String deviceSerial;
    private String startTime;
    private String adb;

    LogcatCapture(String deviceSerial) {
        this.deviceSerial = deviceSerial;
    }

    String deviceSerial() {
        return deviceSerial;
    }

    void start() throws IOException, InterruptedException {
        adb = findAdb();

        if (deviceSerial == null) {
            deviceSerial = resolveSingleDevice();
        }

        String deviceTime = runOnDevice(
                "start log collection",
                "shell",
                "date +%s.%N && log -p i -t "
                        + MARKER_TAG + " BEGIN_" + id
        ).strip();

        if (!deviceTime.matches("\\d+\\.\\d{9}")) {
            throw new IOException(
                    "ADB did not return a device timestamp for the Logcat start boundary");
        }

        startTime = deviceTime;

        LOGGER.debug("Established the Logcat start boundary");
    }

    List<String> collect() throws IOException, InterruptedException {
        if (startTime == null) {
            throw new IOException("Logcat collection has not been initialized");
        }

        runOnDevice(
                "mark the Logcat end boundary",
                "shell", "log", "-p", "i", "-t",
                MARKER_TAG, "END_" + id
        );

        String dump = runOnDevice(
                "collect Logcat",
                "logcat", "-d",
                "-b", "main,system,crash,events",
                "-v", "threadtime",
                "-v", "epoch",
                "-v", "usec",
                "-T", startTime,
                "*:V"
        );

        List<String> logs = scope(dump.lines().toList(), id);

        LOGGER.debug(
                "Collected {} Logcat lines for the test invocation",
                logs.size()
        );

        return logs;
    }

    static List<String> scope(List<String> lines, String id)
            throws IOException {

        int begin = -1;

        for (int index = 0; index < lines.size(); index++) {
            var marker = MARKER.matcher(lines.get(index));

            if (!marker.matches()) {
                continue;
            }

            if (marker.group(1).equals("BEGIN_" + id)) {
                begin = index;
            } else if (marker.group(1).equals("END_" + id)
                    && begin >= 0) {

                return lines.subList(begin, index + 1);
            }
        }

        throw new IOException(
                "Logcat is missing the invocation's start or end marker");
    }

    private String resolveSingleDevice()
            throws IOException, InterruptedException {

        List<String> devices = run(
                "list connected devices", List.of("devices"))
                .lines()
                .filter(line -> line.contains("\t"))
                .toList();

        if (devices.isEmpty()) {
            throw new IOException(
                    "ADB found no connected Android devices");
        }

        if (devices.size() > 1) {
            throw new IOException(
                    "Multiple ADB devices are connected. Set device.udid to select one");
        }

        String[] device = devices.get(0).strip().split("\\s+", 2);

        if (device.length != 2 || !device[1].equals("device")) {
            throw new IOException(
                    "The connected ADB device is offline or unauthorized");
        }

        return device[0];
    }

    private String runOnDevice(String operation, String... arguments)
            throws IOException, InterruptedException {

        List<String> command =
                new ArrayList<>(List.of("-s", deviceSerial));

        command.addAll(List.of(arguments));

        return run(operation, command);
    }

    private String run(String operation, List<String> arguments)
            throws IOException, InterruptedException {

        List<String> command = new ArrayList<>(List.of(adb));
        command.addAll(arguments);

        Path output = Files.createTempFile("mobile-logcat-", ".tmp");
        Path errorOutput = null;

        try {
            errorOutput = Files.createTempFile("mobile-adb-error-", ".tmp");

            Process process = new ProcessBuilder(command)
                    .redirectOutput(output.toFile())
                    .redirectError(errorOutput.toFile())
                    .start();

            try {
                process.getOutputStream().close();

                if (!process.waitFor(
                        COMMAND_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {

                    throw new IOException(
                            "ADB timed out while attempting to " + operation);
                }

                if (process.exitValue() != 0) {
                    String error = Files.readString(
                            errorOutput, StandardCharsets.UTF_8).strip();

                    throw new IOException(
                            "ADB exited with code " + process.exitValue()
                                    + " while attempting to " + operation
                                    + ": " + error);
                }

                return new String(
                        Files.readAllBytes(output),
                        StandardCharsets.UTF_8
                );

            } finally {
                terminateProcess(process);
            }

        } finally {
            deleteTemporaryFile(output);
            deleteTemporaryFile(errorOutput);
        }
    }

    private static void terminateProcess(Process process) {
        if (!process.isAlive()) {
            return;
        }

        process.destroyForcibly();

        try {
            if (!process.waitFor(
                    PROCESS_TERMINATION_TIMEOUT_SECONDS,
                    TimeUnit.SECONDS)) {

                LOGGER.warn(
                        "ADB process did not exit after forcible termination");
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();

            LOGGER.warn(
                    "Interrupted while waiting for ADB process termination",
                    exception
            );
        }
    }

    private static void deleteTemporaryFile(Path path) {
        if (path == null) {
            return;
        }

        try {
            Files.deleteIfExists(path);
        } catch (IOException exception) {
            LOGGER.warn("Failed to delete temporary file: {}", path, exception);
        }
    }

    private static String findAdb() {
        String executable = System.getProperty("os.name")
                .startsWith("Windows") ? "adb.exe" : "adb";

        for (String variable : List.of(
                "ANDROID_HOME", "ANDROID_SDK_ROOT")) {

            String sdk = System.getenv(variable);

            if (sdk != null && !sdk.isBlank()) {
                Path adb = Path.of(sdk, "platform-tools", executable);

                if (Files.isRegularFile(adb)) {
                    return adb.toString();
                }
            }
        }

        return executable;
    }
}