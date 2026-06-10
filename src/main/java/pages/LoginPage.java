package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import base.BasePage;
import utilities.ConfigReader;
import utilities.WaitUtility;

/**
 * Page Object for: https://the-internet.herokuapp.com/login
 *
 * All locators are private. All interactions are exposed via
 * semantic methods — test classes never reference WebElements directly.
 */
public class LoginPage extends BasePage {

    private static final String PAGE_PATH = "/login";

    @FindBy(id = "username")
    private WebElement usernameInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(css = "button[type='submit']")
    private WebElement loginBtn;

    @FindBy(css = "#flash")
    private WebElement flashMessage;

    @FindBy(css = "#flash-messages")
    private WebElement flashContainer;


    public LoginPage(WebDriver driver) {
        super(driver);
    }

    // ─── Navigation ───────────────────────────────────────────────────────────

    public LoginPage open() {
        navigateTo(ConfigReader.getInstance().getBaseUrl() + PAGE_PATH);
        WaitUtility.waitForVisibility(driver, usernameInput);
        log.info("LoginPage opened.");
        return this;
    }

    // ─── Actions ──────────────────────────────────────────────────────────────

    public LoginPage enterUsername(String username) {
        log.info("Entering username: {}", username);
        type(usernameInput, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        log.info("Entering password: [REDACTED]");
        type(passwordInput, password);
        return this;
    }

    public SecureAreaPage clickLoginExpectingSuccess() {
        log.info("Submitting login form (expecting success)");
        click(loginBtn);
        return new SecureAreaPage(driver);
    }

    public LoginPage clickLoginExpectingFailure() {
        log.info("Submitting login form (expecting failure)");
        click(loginBtn);
        return this;
    }

    /**
     * Convenience method — performs full login and returns the next page.
     */
    public SecureAreaPage loginWith(String username, String password) {
        return enterUsername(username)
                .enterPassword(password)
                .clickLoginExpectingSuccess();
    }

    // ─── Assertions support ───────────────────────────────────────────────────

    public String getFlashMessageText() {
        WaitUtility.waitForVisibility(driver, flashMessage);
        return getText(flashMessage);
    }

    public boolean isFlashMessageDisplayed() {
        return isDisplayed(flashMessage);
    }

    public boolean isErrorFlashPresent() {
        String msg = getFlashMessageText();
        return msg.toLowerCase().contains("invalid") || msg.toLowerCase().contains("error");
    }

    public boolean isUsernameFieldEmpty() {
        return usernameInput.getAttribute("value").isEmpty();
    }

    public boolean isPasswordFieldEmpty() {
        return passwordInput.getAttribute("value").isEmpty();
    }

    public boolean isLoginButtonDisplayed() {
        return isDisplayed(loginBtn);
    }
}