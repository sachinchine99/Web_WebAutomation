package tests;

import io.qameta.allure.*;
import pages.LoginPage;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import baseTest.BaseTest;

/**
 * LoginTest — validates login functionality on:
 * https://the-internet.herokuapp.com/login
 *
 * Covers:
 *  1. Valid credentials → successful login
 *  2. Invalid username  → error flash shown
 *  3. Invalid password  → error flash shown
 *  4. Empty username    → error flash shown
 *  5. Empty password    → error flash shown
 */
@Epic("ShopNest Checkout 2.0 — QA Assignment")
@Feature("Login Page")
public class LoginTest extends BaseTest {

    private LoginPage loginPage;

    @BeforeMethod(alwaysRun = true)
    public void openLoginPage() {
        loginPage = new LoginPage(getDriver()).open();
    }

    // ─── TC-LOGIN-01 ──────────────────────────────────────────────────────────

    @Test(
        description = "Valid credentials should redirect to the secure area",
        groups = {"smoke", "login"}
    )
    @Story("Successful Login")
    @Severity(SeverityLevel.BLOCKER)
    public void validLogin_shouldRedirectToSecureArea() {
        var securePage = loginPage
                .enterUsername(config().getValidUsername())
                .enterPassword(config().getValidPassword())
                .clickLoginExpectingSuccess();

        Assert.assertTrue(securePage.isOnSecurePage(),
                "Expected to land on the Secure Area page after valid login.");
        Assert.assertTrue(securePage.isSuccessFlashDisplayed(),
                "Expected a success flash message after login.");
    }

    // ─── TC-LOGIN-02 ──────────────────────────────────────────────────────────

    @Test(
        description = "Invalid username should display an error flash message",
        groups = {"regression", "login"}
    )
    @Story("Invalid Login")
    @Severity(SeverityLevel.CRITICAL)
    public void invalidUsername_shouldShowErrorMessage() {
        loginPage
                .enterUsername("wronguser")
                .enterPassword(config().getValidPassword())
                .clickLoginExpectingFailure();

        Assert.assertTrue(loginPage.isFlashMessageDisplayed(),
                "Flash message should be displayed on invalid username.");
        Assert.assertTrue(loginPage.isErrorFlashPresent(),
                "Flash message should indicate invalid credentials.");
    }

    // ─── TC-LOGIN-03 ──────────────────────────────────────────────────────────

    @Test(
        description = "Invalid password should display an error flash message",
        groups = {"regression", "login"}
    )
    @Story("Invalid Login")
    @Severity(SeverityLevel.CRITICAL)
    public void invalidPassword_shouldShowErrorMessage() {
        loginPage
                .enterUsername(config().getValidUsername())
                .enterPassword("WrongPassword!")
                .clickLoginExpectingFailure();

        Assert.assertTrue(loginPage.isFlashMessageDisplayed(),
                "Flash message should appear after submitting wrong password.");
        Assert.assertTrue(loginPage.isErrorFlashPresent(),
                "Flash message should contain an error indication.");
    }

    // ─── TC-LOGIN-04 ──────────────────────────────────────────────────────────

    @Test(
        description = "Empty username field should trigger an error",
        groups = {"regression", "login", "validation"}
    )
    @Story("Field Validation")
    @Severity(SeverityLevel.NORMAL)
    public void emptyUsername_shouldShowError() {
        loginPage
                .enterUsername("")
                .enterPassword(config().getValidPassword())
                .clickLoginExpectingFailure();

        Assert.assertTrue(loginPage.isFlashMessageDisplayed(),
                "Error flash should be shown when username is empty.");
        Assert.assertTrue(loginPage.isErrorFlashPresent(),
                "Error message should indicate invalid credentials for empty username.");
    }

    // ─── TC-LOGIN-05 ──────────────────────────────────────────────────────────

    @Test(
        description = "Empty password field should trigger an error",
        groups = {"regression", "login", "validation"}
    )
    @Story("Field Validation")
    @Severity(SeverityLevel.NORMAL)
    public void emptyPassword_shouldShowError() {
        loginPage
                .enterUsername(config().getValidUsername())
                .enterPassword("")
                .clickLoginExpectingFailure();

        Assert.assertTrue(loginPage.isFlashMessageDisplayed(),
                "Error flash should be shown when password is empty.");
        Assert.assertTrue(loginPage.isErrorFlashPresent(),
                "Error message should indicate invalid credentials for empty password.");
    }
}