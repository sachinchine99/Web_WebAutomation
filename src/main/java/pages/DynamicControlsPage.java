package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import base.BasePage;
import utilities.ConfigReader;
import utilities.WaitUtility;

/**
 * Page Object for: https://the-internet.herokuapp.com/dynamic_controls
 *
 * This page exercises async DOM changes triggered by button clicks.
 * Strategy: After every button click, wait for the #message element to appear —
 * this is the reliable signal that the async operation has completed.
 * No Thread.sleep() is used anywhere.
 */
public class DynamicControlsPage extends BasePage {

    private static final String PAGE_PATH = "/dynamic_controls";

    // ─── Locators ─────────────────────────────────────────────────────────────

    // Checkbox section
    @FindBy(css = "#checkbox-example input[type='checkbox']")
    private WebElement checkbox;

    @FindBy(css = "#checkbox-example button")
    private WebElement toggleCheckboxButton;

    // Textbox / enable-disable section
    @FindBy(css = "#input-example input[type='text']")
    private WebElement textInput;

    @FindBy(css = "#input-example button")
    private WebElement toggleInputButton;

    // Async message — reliable wait anchor after every async operation
    @FindBy(id = "message")
    private WebElement statusMessage;

    // By-locators used where element may not be in DOM (stale-safe)
    private static final By CHECKBOX_LOCATOR = By.cssSelector("#checkbox-example input[type='checkbox']");
    private static final By MESSAGE_LOCATOR   = By.id("message");

    // ─── Constructor ──────────────────────────────────────────────────────────

    public DynamicControlsPage(WebDriver driver) {
        super(driver);
    }

    // ─── Navigation ───────────────────────────────────────────────────────────

    public DynamicControlsPage open() {
        navigateTo(ConfigReader.getInstance().getBaseUrl() + PAGE_PATH);
        WaitUtility.waitForClickability(driver, toggleCheckboxButton);
        log.info("DynamicControlsPage opened.");
        return this;
    }

    // ─── Checkbox actions ─────────────────────────────────────────────────────

    /**
     * Clicks "Remove" / "Add" button and waits for async completion.
     * The status message ("It's gone!" / "It's back!") is the DOM signal.
     */
    public DynamicControlsPage clickToggleCheckbox() {
        log.info("Clicking checkbox toggle button");
        click(toggleCheckboxButton);
        // Wait for the async message to appear — this confirms the operation finished
        WaitUtility.waitForVisibility(driver, MESSAGE_LOCATOR);
        return this;
    }

    public boolean isCheckboxPresent() {
        try {
            WaitUtility.waitForPresence(driver, CHECKBOX_LOCATOR);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isCheckboxSelected() {
        return isCheckboxPresent() && driver.findElement(CHECKBOX_LOCATOR).isSelected();
    }

    public DynamicControlsPage checkTheCheckbox() {
        if (isCheckboxPresent() && !isCheckboxSelected()) {
            click(driver.findElement(CHECKBOX_LOCATOR));
        }
        return this;
    }

    // ─── Textbox enable/disable actions ──────────────────────────────────────

    /**
     * Clicks "Enable" / "Disable" button and waits for the status message,
     * then additionally waits for the enabled/disabled state to be confirmed.
     */
    public DynamicControlsPage clickToggleInput() {
        log.info("Clicking input toggle button");
        click(toggleInputButton);
        WaitUtility.waitForVisibility(driver, MESSAGE_LOCATOR);
        return this;
    }

    public boolean isTextInputEnabled() {
        return textInput.isEnabled();
    }

    public DynamicControlsPage waitForInputEnabled() {
        WaitUtility.waitForElementEnabled(driver, By.cssSelector("#input-example input[type='text']"));
        log.info("Text input is now enabled");
        return this;
    }

    public DynamicControlsPage waitForInputDisabled() {
        WaitUtility.waitForElementDisabled(driver, By.cssSelector("#input-example input[type='text']"));
        log.info("Text input is now disabled");
        return this;
    }

    public DynamicControlsPage typeInTextInput(String text) {
        log.info("Typing '{}' into text input", text);
        type(textInput, text);
        return this;
    }

    public String getTextInputValue() {
        return textInput.getAttribute("value");
    }

    // ─── Status message ───────────────────────────────────────────────────────

    public String getStatusMessage() {
        WaitUtility.waitForVisibility(driver, statusMessage);
        return getText(statusMessage);
    }

    public boolean isStatusMessageDisplayed() {
        return isDisplayed(statusMessage);
    }
}