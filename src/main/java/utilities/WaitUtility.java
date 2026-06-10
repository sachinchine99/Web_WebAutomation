package utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Centralised explicit wait utility.
 * All wait operations live here — no Thread.sleep() anywhere in the framework.
 * Ignores StaleElementReferenceException and NoSuchElementException during polling.
 */
public final class WaitUtility {

    private static final Logger log = LogManager.getLogger(WaitUtility.class);
    private static final int DEFAULT_TIMEOUT = ConfigReader.getInstance().getExplicitWait();

    private WaitUtility() {}

    // ─── Core WebDriverWait factory ──────────────────────────────────────────

    private static WebDriverWait buildWait(WebDriver driver, int timeoutSeconds) {
        return (WebDriverWait) new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds),
                Duration.ofMillis(300))
                .ignoring(StaleElementReferenceException.class)
                .ignoring(NoSuchElementException.class);
    }

    // ─── Element visibility ───────────────────────────────────────────────────

    public static WebElement waitForVisibility(WebDriver driver, WebElement element) {
        log.debug("Waiting for element to be visible: {}", element);
        return buildWait(driver, DEFAULT_TIMEOUT)
                .until(ExpectedConditions.visibilityOf(element));
    }

    public static WebElement waitForVisibility(WebDriver driver, By locator) {
        log.debug("Waiting for locator to be visible: {}", locator);
        return buildWait(driver, DEFAULT_TIMEOUT)
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitForVisibility(WebDriver driver, WebElement element, int timeoutSeconds) {
        return buildWait(driver, timeoutSeconds)
                .until(ExpectedConditions.visibilityOf(element));
    }

    // ─── Element clickability ─────────────────────────────────────────────────

    public static WebElement waitForClickability(WebDriver driver, WebElement element) {
        log.debug("Waiting for element to be clickable: {}", element);
        return buildWait(driver, DEFAULT_TIMEOUT)
                .until(ExpectedConditions.elementToBeClickable(element));
    }

    public static WebElement waitForClickability(WebDriver driver, By locator) {
        return buildWait(driver, DEFAULT_TIMEOUT)
                .until(ExpectedConditions.elementToBeClickable(locator));
    }

    // ─── Element presence ─────────────────────────────────────────────────────

    public static WebElement waitForPresence(WebDriver driver, By locator) {
        log.debug("Waiting for element presence: {}", locator);
        return buildWait(driver, DEFAULT_TIMEOUT)
                .until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    // ─── Element disappearance ────────────────────────────────────────────────

    public static boolean waitForInvisibility(WebDriver driver, WebElement element) {
        log.debug("Waiting for element to become invisible");
        return buildWait(driver, DEFAULT_TIMEOUT)
                .until(ExpectedConditions.invisibilityOf(element));
    }

    public static boolean waitForInvisibility(WebDriver driver, By locator) {
        return buildWait(driver, DEFAULT_TIMEOUT)
                .until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    // ─── Text conditions ──────────────────────────────────────────────────────

    public static boolean waitForTextPresent(WebDriver driver, WebElement element, String text) {
        log.debug("Waiting for text '{}' in element", text);
        return buildWait(driver, DEFAULT_TIMEOUT)
                .until(ExpectedConditions.textToBePresentInElement(element, text));
    }

    // ─── Attribute conditions ─────────────────────────────────────────────────

    public static boolean waitForAttributeContains(WebDriver driver, WebElement element,
                                                    String attribute, String value) {
        log.debug("Waiting for attribute '{}' to contain '{}'", attribute, value);
        return buildWait(driver, DEFAULT_TIMEOUT)
                .until(ExpectedConditions.attributeContains(element, attribute, value));
    }

    // ─── Element enabled/disabled ─────────────────────────────────────────────

    public static WebElement waitForElementEnabled(WebDriver driver, By locator) {
        log.debug("Waiting for element to be enabled: {}", locator);
        return buildWait(driver, DEFAULT_TIMEOUT).until(driver1 -> {
            WebElement el = driver1.findElement(locator);
            return el.isEnabled() ? el : null;
        });
    }

    public static boolean waitForElementDisabled(WebDriver driver, By locator) {
        log.debug("Waiting for element to be disabled: {}", locator);
        return buildWait(driver, DEFAULT_TIMEOUT).until(driver1 -> {
            WebElement el = driver1.findElement(locator);
            return !el.isEnabled();
        });
    }

    // ─── URL / title conditions ───────────────────────────────────────────────

    public static boolean waitForUrlContains(WebDriver driver, String fragment) {
        log.debug("Waiting for URL to contain: {}", fragment);
        return buildWait(driver, DEFAULT_TIMEOUT)
                .until(ExpectedConditions.urlContains(fragment));
    }

    public static boolean waitForTitleContains(WebDriver driver, String title) {
        return buildWait(driver, DEFAULT_TIMEOUT)
                .until(ExpectedConditions.titleContains(title));
    }

    // ─── Custom condition ─────────────────────────────────────────────────────

    public static <T> T waitFor(WebDriver driver, ExpectedCondition<T> condition, int timeoutSeconds) {
        return buildWait(driver, timeoutSeconds).until(condition);
    }
}