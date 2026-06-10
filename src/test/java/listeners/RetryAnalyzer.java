package listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import utilities.ConfigReader;

/**
 * RetryAnalyzer — retries flaky tests up to the configured retry count.
 *
 * Applied via @Test(retryAnalyzer = RetryAnalyzer.class) or globally
 * through TestListener#onTestStart.
 *
 * Design note: retry count is kept intentionally low (default: 1).
 * Retrying masks genuine failures; this is a safety net, not a crutch.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger log = LogManager.getLogger(RetryAnalyzer.class);
    private static final int MAX_RETRY_COUNT = ConfigReader.getInstance().getRetryCount();
    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRY_COUNT) {
            retryCount++;
            log.warn("Retrying test '{}' — attempt {}/{}",
                    result.getMethod().getMethodName(), retryCount, MAX_RETRY_COUNT);
            return true;
        }
        log.error("Test '{}' failed after {} retry attempt(s). Marking as FAILED.",
                result.getMethod().getMethodName(), MAX_RETRY_COUNT);
        return false;
    }
}