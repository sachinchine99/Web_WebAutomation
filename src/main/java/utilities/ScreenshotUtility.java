package utilities;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Captures and persists screenshots to disk.
 * Returns the absolute path for embedding into Extent / Allure reports.
 */
public final class ScreenshotUtility {

    private static final Logger log = LogManager.getLogger(ScreenshotUtility.class);
    private static final DateTimeFormatter TIMESTAMP_FMT =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private ScreenshotUtility() {}

    /**
     * Captures a screenshot and saves it under the configured screenshot directory.
     *
     * @param driver   active WebDriver instance
     * @param testName used as part of the filename for traceability
     * @return absolute path of saved screenshot, or empty string on failure
     */
    public static String capture(WebDriver driver, String testName) {
        if (driver == null) {
            log.warn("Screenshot skipped — driver is null for test: {}", testName);
            return "";
        }

        try {
            String dir = ConfigReader.getInstance().getScreenshotDir();
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FMT);
            String safeName = testName.replaceAll("[^a-zA-Z0-9_\\-]", "_");
            String fileName = safeName + "_" + timestamp + ".png";

            File destDir = new File(dir);
            if (!destDir.exists() && !destDir.mkdirs()) {
                log.error("Failed to create screenshot directory: {}", dir);
                return "";
            }

            File destFile = new File(destDir, fileName);
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(srcFile, destFile);

            String absolutePath = destFile.getAbsolutePath();
            log.info("Screenshot saved: {}", absolutePath);
            return absolutePath;

        } catch (IOException e) {
            log.error("Screenshot capture failed for test '{}': {}", testName, e.getMessage());
            return "";
        }
    }

    /**
     * Captures screenshot as byte array — used for attaching directly to Allure reports.
     */
    public static byte[] captureAsBytes(WebDriver driver) {
        if (driver == null) return new byte[0];
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            log.error("Failed to capture screenshot as bytes: {}", e.getMessage());
            return new byte[0];
        }
    }
}