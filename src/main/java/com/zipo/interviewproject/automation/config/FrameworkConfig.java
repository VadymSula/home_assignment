package com.zipo.interviewproject.automation.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Optional;
import java.util.Properties;

/** Immutable configuration for an Appium test environment. */
public record FrameworkConfig(
        URL appiumServerUrl,
        String deviceName,
        Optional<String> deviceUdid,
        String platformName,
        String platformVersion,
        String automationName,
        Path apkPath,
        String appPackage,
        Optional<String> appActivity,
        Duration explicitWaitTimeout,
        Optional<Integer> systemPort) {

    private static final Logger LOGGER = LoggerFactory.getLogger(FrameworkConfig.class);

    private static final String DEFAULTS_RESOURCE = "/framework.properties";
    private static final String SERVER_URL_KEY = "appium.server.url";
    private static final String DEVICE_NAME_KEY = "device.name";
    private static final String DEVICE_UDID_KEY = "device.udid";
    private static final String PLATFORM_NAME_KEY = "platform.name";
    private static final String PLATFORM_VERSION_KEY = "platform.version";
    private static final String AUTOMATION_NAME_KEY = "automation.name";
    private static final String APK_PATH_KEY = "app.apk.path";
    private static final String APP_PACKAGE_KEY = "app.package";
    private static final String APP_ACTIVITY_KEY = "app.activity";
    private static final String WAIT_TIMEOUT_KEY = "wait.timeout.seconds";
    private static final String SYSTEM_PORT_KEY = "uiautomator2.system.port";

    public static FrameworkConfig load() {
        Properties defaults = new Properties();

        try (InputStream input = FrameworkConfig.class
                .getResourceAsStream(DEFAULTS_RESOURCE)) {

            if (input == null) {
                LOGGER.error("Missing framework configuration resource: {}",
                        DEFAULTS_RESOURCE);

                throw new IllegalStateException(
                        "Missing classpath resource " + DEFAULTS_RESOURCE);
            }

            defaults.load(input);

        } catch (IOException exception) {
            LOGGER.error("Could not read framework configuration ({})",
                    exception.getClass().getSimpleName());

            throw new UncheckedIOException(
                    "Could not load " + DEFAULTS_RESOURCE, exception);
        }

        try {
            FrameworkConfig config = new FrameworkConfig(
                    parseServerUrl(required(defaults, SERVER_URL_KEY)),
                    required(defaults, DEVICE_NAME_KEY),
                    optional(defaults, DEVICE_UDID_KEY),
                    required(defaults, PLATFORM_NAME_KEY),
                    required(defaults, PLATFORM_VERSION_KEY),
                    required(defaults, AUTOMATION_NAME_KEY),
                    Path.of(required(defaults, APK_PATH_KEY)),
                    required(defaults, APP_PACKAGE_KEY),
                    optional(defaults, APP_ACTIVITY_KEY),
                    Duration.ofSeconds(parseInt(
                            WAIT_TIMEOUT_KEY,
                            required(defaults, WAIT_TIMEOUT_KEY),
                            Integer.MAX_VALUE)),
                    optional(defaults, SYSTEM_PORT_KEY)
                            .map(value -> parseInt(SYSTEM_PORT_KEY, value, 65535))
            );

            LOGGER.debug(
                    "Framework configuration loaded: platform={}, version={}, automation={}, explicitWait={}",
                    config.platformName(),
                    config.platformVersion(),
                    config.automationName(),
                    config.explicitWaitTimeout());

            return config;

        } catch (IllegalArgumentException exception) {
            LOGGER.error("Framework configuration validation failed ({})",
                    exception.getClass().getSimpleName());

            throw exception;
        }
    }

    private static String required(Properties defaults, String key) {
        return optional(defaults, key)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Missing value for " + key));
    }

    private static Optional<String> optional(Properties defaults, String key) {
        String value = System.getProperty(
                key, defaults.getProperty(key, "")).strip();

        return value.isEmpty() ? Optional.empty() : Optional.of(value);
    }

    private static URL parseServerUrl(String value) {
        String message =
                SERVER_URL_KEY + " must be an absolute HTTP or HTTPS URL";

        try {
            URI uri = URI.create(value);
            String scheme = uri.getScheme();

            if (uri.getHost() != null
                    && ("http".equalsIgnoreCase(scheme)
                    || "https".equalsIgnoreCase(scheme))) {
                return uri.toURL();
            }

        } catch (IllegalArgumentException | MalformedURLException exception) {
            throw new IllegalArgumentException(message, exception);
        }

        throw new IllegalArgumentException(message);
    }

    private static int parseInt(String key, String value, int maximum) {
        String message = key + " must be an integer between 1 and " + maximum;
        int number;

        try {
            number = Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(message, exception);
        }

        if (number < 1 || number > maximum) {
            throw new IllegalArgumentException(message);
        }

        return number;
    }
}