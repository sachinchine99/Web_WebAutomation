package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import base.BasePage;
import utilities.WaitUtility;

/**
 * Page Object for: https://the-internet.herokuapp.com/secure
 *
 * Represents the secure area that is only accessible after a successful login.
 * Validates redirect destination and provides logout capability.
 */
public class SecureAreaPage extends BasePage {

    private static final String SECURE_URL_FRAGMENT = "/secure";
    private static final String EXPECTED_HEADING = "Secure Area";
    private static final String SUCCESS_FLASH_FRAGMENT = "logged in";

    // ─── Locators ─────────────────────────────────────────────────────────────

    @FindBy(css = "h2")
    private WebElement pageHeading;

    @FindBy(css = "#flash")
    private WebElement flashMessage;

    @FindBy(css = "a.button.secondary[href='/logout']")
    private WebElement logoutButton;

    @FindBy(css = "div.example")
    private WebElement contentArea;

    // ─── Constructor ──────────────────────────────────────────────────────────

    public SecureAreaPage(WebDriver driver) {
        super(driver);
        // Wait for the page to fully load before any assertions are made
        WaitUtility.waitForVisibility(driver, pageHeading);
    }

    // ─── Actions ──────────────────────────────────────────────────────────────

    public LoginPage clickLogout() {
        log.info("Clicking logout button");
        click(logoutButton);
        return new LoginPage(driver);
    }

    // ─── Assertion support ────────────────────────────────────────────────────

    public boolean isOnSecurePage() {
        boolean urlMatch = getCurrentUrl().contains(SECURE_URL_FRAGMENT);
        boolean headingMatch = getText(pageHeading).contains(EXPECTED_HEADING);
        log.info("SecureAreaPage validation — URL match: {}, Heading match: {}", urlMatch, headingMatch);
        return urlMatch && headingMatch;
    }

    public String getPageHeadingText() {
        return getText(pageHeading);
    }

    public String getFlashMessageText() {
        WaitUtility.waitForVisibility(driver, flashMessage);
        return getText(flashMessage);
    }

    public boolean isSuccessFlashDisplayed() {
        String msg = getFlashMessageText().toLowerCase();
        return msg.contains(SUCCESS_FLASH_FRAGMENT);
    }

    public boolean isLogoutButtonDisplayed() {
        return isDisplayed(logoutButton);
    }

    public boolean isUrlCorrect() {
        return getCurrentUrl().contains(SECURE_URL_FRAGMENT);
    }
}