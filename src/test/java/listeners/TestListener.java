package listeners;


import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import io.qameta.allure.Allure;
import utilities.ConfigReader;
import utilities.DriverManager;
import utilities.ExtentReportManager;
import utilities.ScreenshotUtility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.IRetryAnalyzer;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;

/**
 * TestListener — wires together ExtentReports, Allure, and screenshot-on-failure.
 * Registered in testng.xml so it applies globally without @Listeners annotation
 * on individual test classes.
 *
 * Responsibilities:
 *  - Create an ExtentTest entry per test
 *  - On failure: capture screenshot, attach to both Extent and Allure
 *  - Apply RetryAnalyzer globally
 *  - Log pass/fail/skip to ExtentReports
 */
public class TestListener implements ITestListener {

    private static final Logger log = LogManager.getLogger(TestListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();
        log.info("▶  TEST STARTED: {}", testName);

        // Attach RetryAnalyzer globally if not already set
        IRetryAnalyzer existing = result.getMethod().getRetryAnalyzer(result);
        if (existing == null) {
            result.getMethod().setRetryAnalyzerClass(RetryAnalyzer.class);
        }

        ExtentTest test = ExtentReportManager.createTest(
                testName,
                description != null ? description : testName
        );
        test.assignCategory(result.getTestClass().getName()
                .substring(result.getTestClass().getName().lastIndexOf('.') + 1));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("✔  PASSED: {}", result.getMethod().getMethodName());
        ExtentTest test = ExtentReportManager.getTest();
        if (test != null) {
            test.log(Status.PASS, "Test PASSED");
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        log.error("✘  FAILED: {} — {}", testName, result.getThrowable().getMessage());

        ExtentTest test = ExtentReportManager.getTest();
        if (test != null) {
            test.log(Status.FAIL, result.getThrowable());
        }

        if (ConfigReader.getInstance().isScreenshotOnFail()) {
            captureAndAttachScreenshot(testName, test);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("⏭  SKIPPED: {}", result.getMethod().getMethodName());
        ExtentTest test = ExtentReportManager.getTest();
        if (test != null) {
            test.log(Status.SKIP, "Test SKIPPED: "
                    + (result.getThrowable() != null ? result.getThrowable().getMessage() : "No reason given"));
        }
    }

    // ─── Screenshot helper ────────────────────────────────────────────────────

    private void captureAndAttachScreenshot(String testName, ExtentTest extentTest) {
        try {
            WebDriver driver = DriverManager.getDriver();

            // 1. Save to disk and attach to Extent HTML report
            String screenshotPath = ScreenshotUtility.capture(driver, testName);
            if (!screenshotPath.isEmpty() && extentTest != null) {
                extentTest.fail("Screenshot on failure:",
                        MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
            }

            // 2. Attach bytes to Allure report
            byte[] screenshotBytes = ScreenshotUtility.captureAsBytes(driver);
            if (screenshotBytes.length > 0) {
                Allure.addAttachment(testName + "_failure",
                        "image/png",
                        new ByteArrayInputStream(screenshotBytes),
                        ".png");
            }

        } catch (Exception e) {
            log.warn("Could not capture screenshot for test '{}': {}", testName, e.getMessage());
        }
    }
}