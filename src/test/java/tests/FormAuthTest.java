package tests;

import io.qameta.allure.*;
import pages.LoginPage;
import pages.SecureAreaPage;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import baseTest.BaseTest;

/**
 * FormAuthTest — validates the full form authentication flow:
 * login → redirect → secure area content → logout
 *
 * https://the-internet.herokuapp.com/login
 */
@Epic("ShopNest Checkout 2.0 — QA Assignment")
@Feature("Form Authentication")
public class FormAuthTest extends BaseTest {

    private LoginPage loginPage;

    @BeforeMethod(alwaysRun = true)
    public void openLoginPage() {
        loginPage = new LoginPage(getDriver()).open();
    }

    // ─── TC-AUTH-01 ───────────────────────────────────────────────────────────

    @Test(
        description = "Successful login should redirect to /secure URL",
        groups = {"smoke", "auth"}
    )
    @Story("Successful Login Redirect")
    @Severity(SeverityLevel.BLOCKER)
    public void successfulLogin_shouldRedirectToSecureUrl() {
        SecureAreaPage securePage = loginPage.loginWith(
                config().getValidUsername(),
                config().getValidPassword()
        );

        Assert.assertTrue(securePage.isUrlCorrect(),
                "URL should contain '/secure' after successful login. Actual: "
                        + securePage.getCurrentUrl());
    }

    // ─── TC-AUTH-02 ───────────────────────────────────────────────────────────

    @Test(
        description = "Secure area page should display the expected heading after login",
        groups = {"smoke", "auth"}
    )
    @Story("Secure Area Content")
    @Severity(SeverityLevel.CRITICAL)
    public void successfulLogin_shouldDisplaySecureAreaHeading() {
        SecureAreaPage securePage = loginPage.loginWith(
                config().getValidUsername(),
                config().getValidPassword()
        );

        Assert.assertTrue(securePage.isOnSecurePage(),
                "Heading should read 'Secure Area'. Actual: " + securePage.getPageHeadingText());
    }

    // ─── TC-AUTH-03 ───────────────────────────────────────────────────────────

    @Test(
        description = "Success flash message should appear on the secure area page",
        groups = {"regression", "auth"}
    )
    @Story("Login Flash Notification")
    @Severity(SeverityLevel.NORMAL)
    public void successfulLogin_shouldShowSuccessFlash() {
        SecureAreaPage securePage = loginPage.loginWith(
                config().getValidUsername(),
                config().getValidPassword()
        );

        Assert.assertTrue(securePage.isSuccessFlashDisplayed(),
                "A success flash message should be visible after login. Actual: "
                        + securePage.getFlashMessageText());
    }

    // ─── TC-AUTH-04 ───────────────────────────────────────────────────────────

    @Test(
        description = "Logout should redirect back to the login page and show a logged-out flash",
        groups = {"smoke", "auth"}
    )
    @Story("Logout Flow")
    @Severity(SeverityLevel.BLOCKER)
    public void logout_shouldRedirectToLoginPage() {
        SecureAreaPage securePage = loginPage.loginWith(
                config().getValidUsername(),
                config().getValidPassword()
        );

        Assert.assertTrue(securePage.isLogoutButtonDisplayed(),
                "Logout button should be present on the secure area page.");

        LoginPage returnedLoginPage = securePage.clickLogout();

        // After logout, we should be back on the login page
        Assert.assertTrue(returnedLoginPage.getCurrentUrl().contains("/login"),
                "After logout, URL should return to /login. Actual: "
                        + returnedLoginPage.getCurrentUrl());

        Assert.assertTrue(returnedLoginPage.isFlashMessageDisplayed(),
                "A logout confirmation flash should be displayed on the login page.");

        // Verify the flash is not an error (logged out ≠ error)
        String flashText = returnedLoginPage.getFlashMessageText().toLowerCase();
        Assert.assertTrue(flashText.contains("logged out"),
                "Flash message should confirm logout. Actual: " + flashText);
    }
}