package baseTest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

import utilities.ConfigReader;
import utilities.DriverManager;
import utilities.ExtentReportManager;

/**
 * BaseTest — parent of all test classes.
 *
 * Lifecycle:
 *   @BeforeSuite  → initialise ExtentReports (once per run)
 *   @BeforeMethod → spin up a fresh WebDriver per test (thread-safe)
 *   @AfterMethod  → quit driver; log test result
 *   @AfterSuite   → flush all reports
 *
 * Tests inherit WebDriver access via getDriver() — no public driver field,
 * preventing accidental direct manipulation from test classes.
 */
public abstract class BaseTest {

    private static final Logger log = LogManager.getLogger(BaseTest.class);

    @BeforeSuite(alwaysRun = true)
    public void suiteSetup() {
        log.info("===== SUITE START =====");
        ExtentReportManager.getExtentReports(); // Initialise once
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp(ITestResult result) {
        log.info("----- Test START: {} -----", result.getMethod().getMethodName());
        DriverManager.initDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        log.info("----- Test END: {} | Status: {} -----",
                result.getMethod().getMethodName(),
                statusLabel(result.getStatus()));
        DriverManager.quitDriver();
        ExtentReportManager.removeTest();
    }

    @AfterSuite(alwaysRun = true)
    public void suiteTeardown() {
        ExtentReportManager.flush();
        log.info("===== SUITE END =====");
    }

    // ─── Accessor ─────────────────────────────────────────────────────────────

    protected WebDriver getDriver() {
        return DriverManager.getDriver();
    }

    protected ConfigReader config() {
        return ConfigReader.getInstance();
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private String statusLabel(int status) {
        return switch (status) {
            case ITestResult.SUCCESS -> "PASSED";
            case ITestResult.FAILURE -> "FAILED";
            case ITestResult.SKIP    -> "SKIPPED";
            default                  -> "UNKNOWN";
        };
    }
}