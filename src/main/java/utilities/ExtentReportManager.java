package utilities;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Manages the lifecycle of the ExtentReports HTML report.
 * ExtentTest instances are stored per-thread for parallel safety.
 */
public final class ExtentReportManager {

    private static final Logger log = LogManager.getLogger(ExtentReportManager.class);
    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> testThread = new ThreadLocal<>();

    private ExtentReportManager() {}

    public static synchronized ExtentReports getExtentReports() {
        if (extent == null) {
            ConfigReader cfg = ConfigReader.getInstance();
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
            String reportPath = cfg.getExtentReportDir() + File.separator
                    + timestamp + "_" + cfg.getExtentReportName();

            new File(cfg.getExtentReportDir()).mkdirs();

            ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
            spark.config().setTheme(Theme.DARK);
            spark.config().setDocumentTitle("ShopNest Automation Report");
            spark.config().setReportName("UI Regression Suite — the-internet.herokuapp.com");
            spark.config().setEncoding("UTF-8");

            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Environment", cfg.getBaseUrl());
            extent.setSystemInfo("Browser", cfg.getBrowser());
            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("Java", System.getProperty("java.version"));
            extent.setSystemInfo("Author", "Sachin — QA Automation Engineer");

            log.info("ExtentReports initialised at: {}", reportPath);
        }
        return extent;
    }

    public static ExtentTest createTest(String testName, String description) {
        ExtentTest test = getExtentReports().createTest(testName, description);
        testThread.set(test);
        return test;
    }

    public static ExtentTest getTest() {
        return testThread.get();
    }

    public static void removeTest() {
        testThread.remove();
    }

    public static synchronized void flush() {
        if (extent != null) {
            extent.flush();
            log.info("ExtentReports flushed successfully.");
        }
    }
}