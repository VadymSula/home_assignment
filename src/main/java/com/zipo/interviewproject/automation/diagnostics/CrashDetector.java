package com.zipo.interviewproject.automation.diagnostics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Identifies Android-reported application crashes and ANRs in scoped Logcat output. */
final class CrashDetector {

    private static final Pattern LOG_LINE = Pattern.compile(
            "^\\s*(\\d+\\.\\d+)\\s+(\\d+)\\s+\\d+\\s+[VDIWEFAS]\\s+(\\S+?)\\s*:\\s?(.*)$");

    private static final Pattern PROCESS_EVENT = Pattern.compile(
            "^\\[\\s*-?\\d+\\s*,\\s*(\\d+)\\s*,\\s*([^,\\]\\s]+)\\s*[,\\]].*$");

    private static final Pattern PROCESS_START = Pattern.compile(
            "^\\[\\s*-?\\d+\\s*,\\s*(\\d+)\\s*,\\s*\\d+\\s*,\\s*([^,\\]\\s]+)\\s*,.*$");

    private static final Pattern RECOVERABLE_CRASH = Pattern.compile(
            ",\\s*-?\\d+\\s*,\\s*1\\s*]$");

    private static final Pattern JAVA_PROCESS = Pattern.compile(
            "^Process: (\\S+), PID: (\\d+)$");

    private static final Pattern TEXT_ANR = Pattern.compile(
            "^ANR in (\\S+)(?:\\s.*)?$");

    private static final Pattern TOMBSTONE_PROCESS = Pattern.compile(
            "^pid:\\s*(\\d+), tid:.*>>>\\s*(\\S+)\\s*<<<$");

    private static final String FATAL_SIGNALS =
            "SIG(?:ABRT|BUS|FPE|ILL|SEGV|SYS|TRAP|STKFLT)";

    private static final Pattern LIBC_SIGNAL = Pattern.compile(
            "^Fatal signal \\d+ \\(" + FATAL_SIGNALS
                    + "\\).*\\bin tid \\d+ .*\\bpid (\\d+)\\b");

    private static final Pattern TOMBSTONE_SIGNAL = Pattern.compile(
            "^signal \\d+ \\(" + FATAL_SIGNALS + "\\),");

    private static final int UNKNOWN_PID = 0;
    private static final double REPORT_LINE_WINDOW_SECONDS = 5;
    private static final double NATIVE_REPORT_WINDOW_SECONDS = 30;

    private final String appPackage;

    CrashDetector(String appPackage) {
        this.appPackage = appPackage;
    }

    List<Finding> detect(List<String> scopedLogs) {
        List<Finding> findings = new ArrayList<>();
        List<NativeSignal> nativeSignals = new ArrayList<>();

        Map<Integer, String> processNames = new HashMap<>();
        Map<Integer, Double> javaFatalHeaders = new HashMap<>();
        Map<Integer, TombstoneHeader> tombstones = new HashMap<>();

        for (String line : scopedLogs) {
            Matcher entry = LOG_LINE.matcher(line);

            if (!entry.matches()) {
                continue;
            }

            double timestamp = Double.parseDouble(entry.group(1));
            int loggerPid = Integer.parseInt(entry.group(2));
            String tag = entry.group(3);
            String message = entry.group(4).strip();

            switch (tag) {
                case "am_proc_start" -> {
                    Matcher process = PROCESS_START.matcher(message);

                    if (process.matches()) {
                        int pid = Integer.parseInt(process.group(1));

                        finalizeNativeSignals(
                                findings, nativeSignals, pid);

                        processNames.put(pid, process.group(2));
                        javaFatalHeaders.remove(pid);
                    }
                }

                case "am_proc_died" -> {
                    Matcher process = PROCESS_EVENT.matcher(message);

                    if (process.matches()) {
                        int pid = Integer.parseInt(process.group(1));
                        String name = process.group(2);

                        finalizeNativeSignals(
                                findings, nativeSignals, pid);

                        processNames.remove(pid, name);
                        javaFatalHeaders.remove(pid);
                    }
                }

                case "am_crash" -> {
                    Matcher process = PROCESS_EVENT.matcher(message);

                    if (process.matches()) {
                        String name = process.group(2);

                        if (RECOVERABLE_CRASH.matcher(message).find()) {
                            discardRecoverableSignal(
                                    nativeSignals, name, timestamp);
                        } else {
                            addFinding(findings,
                                    new Finding(
                                            Kind.CRASH, name, UNKNOWN_PID));
                        }
                    }
                }

                case "am_anr" -> {
                    Matcher process = PROCESS_EVENT.matcher(message);

                    if (process.matches()) {
                        addFinding(findings,
                                new Finding(
                                        Kind.ANR,
                                        process.group(2),
                                        Integer.parseInt(process.group(1))));
                    }
                }

                case "AndroidRuntime" -> {
                    if (message.startsWith("FATAL EXCEPTION:")) {
                        javaFatalHeaders.put(loggerPid, timestamp);
                    }

                    Matcher process = JAVA_PROCESS.matcher(message);

                    if (process.matches()
                            && Integer.parseInt(process.group(2)) == loggerPid
                            && follows(javaFatalHeaders.remove(loggerPid),
                            timestamp)) {

                        addFinding(findings,
                                new Finding(
                                        Kind.CRASH,
                                        process.group(1),
                                        loggerPid));
                    }
                }

                case "ActivityManager" -> {
                    Matcher process = TEXT_ANR.matcher(message);

                    if (process.matches()) {
                        addFinding(findings,
                                new Finding(
                                        Kind.ANR,
                                        process.group(1),
                                        UNKNOWN_PID));
                    }
                }

                case "libc" -> {
                    Matcher signal = LIBC_SIGNAL.matcher(message);

                    if (signal.find()
                            && Integer.parseInt(signal.group(1)) == loggerPid) {

                        addNativeSignal(
                                nativeSignals,
                                processNames.get(loggerPid),
                                loggerPid,
                                timestamp);
                    }
                }

                case "DEBUG" -> {
                    Matcher process = TOMBSTONE_PROCESS.matcher(message);

                    if (process.matches()) {
                        tombstones.put(loggerPid,
                                new TombstoneHeader(
                                        timestamp,
                                        Integer.parseInt(process.group(1)),
                                        process.group(2)));

                    } else if (TOMBSTONE_SIGNAL.matcher(message).find()) {
                        TombstoneHeader header =
                                tombstones.remove(loggerPid);

                        if (header != null
                                && follows(header.timestamp(), timestamp)) {

                            addNativeSignal(
                                    nativeSignals,
                                    header.process(),
                                    header.pid(),
                                    timestamp);
                        }
                    }
                }

                default -> {
                    // Other records remain available in the failure artifact.
                }
            }
        }

        nativeSignals.forEach(
                signal -> addFinding(findings, signal.finding()));

        return List.copyOf(findings);
    }

    private void addNativeSignal(
            List<NativeSignal> nativeSignals,
            String process,
            int pid,
            double timestamp) {

        if (isApplicationProcess(process)) {
            nativeSignals.add(
                    new NativeSignal(
                            new Finding(Kind.CRASH, process, pid),
                            timestamp));
        }
    }

    private void finalizeNativeSignals(
            List<Finding> findings,
            List<NativeSignal> nativeSignals,
            int pid) {

        nativeSignals.removeIf(signal -> {
            if (signal.finding().pid() != pid) {
                return false;
            }

            addFinding(findings, signal.finding());
            return true;
        });
    }

    private static void discardRecoverableSignal(
            List<NativeSignal> nativeSignals,
            String process,
            double timestamp) {

        for (int index = nativeSignals.size() - 1; index >= 0; index--) {
            NativeSignal signal = nativeSignals.get(index);

            if (signal.finding().process().equals(process)
                    && isRecentNativeSignal(signal, timestamp)) {

                int pid = signal.finding().pid();

                nativeSignals.removeIf(candidate ->
                        candidate.finding().pid() == pid
                                && candidate.finding().process().equals(process)
                                && isRecentNativeSignal(candidate, timestamp));

                return;
            }
        }
    }

    private static boolean isRecentNativeSignal(
            NativeSignal signal,
            double timestamp) {

        double elapsed = timestamp - signal.timestamp();

        return elapsed >= 0
                && elapsed <= NATIVE_REPORT_WINDOW_SECONDS;
    }

    private void addFinding(
            List<Finding> findings,
            Finding finding) {

        if (!isApplicationProcess(finding.process())) {
            return;
        }

        for (int index = 0; index < findings.size(); index++) {
            Finding previous = findings.get(index);

            if (previous.kind() == finding.kind()
                    && previous.process().equals(finding.process())
                    && (previous.pid() == finding.pid()
                    || previous.pid() == UNKNOWN_PID
                    || finding.pid() == UNKNOWN_PID)) {

                if (previous.pid() == UNKNOWN_PID) {
                    findings.set(index, finding);
                }

                return;
            }
        }

        findings.add(finding);
    }

    private boolean isApplicationProcess(String process) {
        return process != null
                && (process.equals(appPackage)
                || process.startsWith(appPackage + ":"));
    }

    private static boolean follows(
            Double headerTime,
            double timestamp) {

        return headerTime != null
                && timestamp >= headerTime
                && timestamp - headerTime <= REPORT_LINE_WINDOW_SECONDS;
    }

    enum Kind {
        CRASH,
        ANR
    }

    record Finding(Kind kind, String process, int pid) {

        String description() {
            return kind + " in " + process
                    + (pid == UNKNOWN_PID ? "" : " (PID " + pid + ")");
        }
    }

    private record NativeSignal(
            Finding finding,
            double timestamp) {
    }

    private record TombstoneHeader(
            double timestamp,
            int pid,
            String process) {
    }
}