package utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public final class ConfigReader {

    private static final Logger log = LogManager.getLogger(ConfigReader.class);
    private static final String CONFIG_PATH = "config/config.properties";
    private static ConfigReader instance;
    private final Properties properties;

    private ConfigReader() {
        properties = new Properties();
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(CONFIG_PATH)) {
            if (stream == null) {
                throw new RuntimeException("Config file not found on classpath: " + CONFIG_PATH);
            }
            properties.load(stream);
            log.info("Configuration loaded from: {}", CONFIG_PATH);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration: " + CONFIG_PATH, e);
        }
    }

    public static synchronized ConfigReader getInstance() {
        if (instance == null) {
            instance = new ConfigReader();
        }
        return instance;
    }

    /**
     * Returns property value. Environment variable with the same key (uppercased,
     * dots replaced by underscores) takes precedence — supports CI secret injection.
     */
    public String get(String key) {
        String envKey = key.toUpperCase().replace(".", "_");
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isBlank()) {
            log.debug("Config key '{}' resolved from environment variable '{}'", key, envKey);
            return envValue;
        }
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Missing configuration key: " + key);
        }
        return value.trim();
    }

    public String getBaseUrl()          { return get("base.url"); }
    public String getBrowser()          { return get("browser"); }
    public boolean isHeadless()         { return Boolean.parseBoolean(get("headless")); }
    public int getExplicitWait()        { return Integer.parseInt(get("explicit.wait")); }
    public int getPageLoadTimeout()     { return Integer.parseInt(get("page.load.timeout")); }
    public String getValidUsername()    { return get("valid.username"); }
    public String getValidPassword()    { return get("valid.password"); }
    public boolean isScreenshotOnFail() { return Boolean.parseBoolean(get("screenshot.on.failure")); }
    public String getScreenshotDir()    { return get("screenshot.dir"); }
    public int getRetryCount()          { return Integer.parseInt(get("retry.count")); }
    public String getExtentReportDir()  { return get("extent.report.dir"); }
    public String getExtentReportName() { return get("extent.report.name"); }
}