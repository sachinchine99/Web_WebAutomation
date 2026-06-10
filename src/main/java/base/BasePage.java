package base;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;

import utilities.WaitUtility;


public abstract class BasePage {

    protected final WebDriver driver;
    protected final Logger log;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.log = LogManager.getLogger(this.getClass());
        PageFactory.initElements(driver, this);
    }

    // ─── Navigation ───────────────────────────────────────────────────────────

    protected void navigateTo(String url) {
        log.info("Navigating to: {}", url);
        driver.get(url);
    }

    // ─── Element interactions ─────────────────────────────────────────────────

    protected void click(WebElement element) {
        WaitUtility.waitForClickability(driver, element).click();
        log.debug("Clicked element: {}", element);
    }

    protected void type(WebElement element, String text) {
        WaitUtility.waitForVisibility(driver, element).clear();
        element.sendKeys(text);
        log.debug("Typed '{}' into element", text);
    }

    protected String getText(WebElement element) {
        String text = WaitUtility.waitForVisibility(driver, element).getText().trim();
        log.debug("Got text: '{}'", text);
        return text;
    }

    protected boolean isDisplayed(WebElement element) {
        try {
            return WaitUtility.waitForVisibility(driver, element).isDisplayed();
        } catch (Exception e) {
            log.debug("Element not displayed: {}", e.getMessage());
            return false;
        }
    }

    protected boolean isEnabled(WebElement element) {
        try {
            return element.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    // ─── JavaScript utilities ─────────────────────────────────────────────────

    protected void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    protected void highlightElement(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].style.border='3px solid red'", element);
    }

    // ─── Page state ───────────────────────────────────────────────────────────

    public String getPageTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    // ─── Actions ──────────────────────────────────────────────────────────────

    protected Actions buildActions() {
        return new Actions(driver);
    }
}