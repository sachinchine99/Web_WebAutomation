package tests;

import io.qameta.allure.*;
import pages.DynamicControlsPage;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import baseTest.BaseTest;

/**
 * DynamicControlsTest — exercises async DOM operations on:
 * https://the-internet.herokuapp.com/dynamic_controls
 *
 * Critical design note: Every assertion here waits on an
 * explicit DOM signal — no Thread.sleep() anywhere.
 */
@Epic("ShopNest Checkout 2.0 — QA Assignment")
@Feature("Dynamic Controls")
public class DynamicControlsTest extends BaseTest {

    private DynamicControlsPage dynamicControlsPage;

    @BeforeMethod(alwaysRun = true)
    public void openPage() {
        dynamicControlsPage = new DynamicControlsPage(getDriver()).open();
    }

    // ─── TC-DYN-01 ────────────────────────────────────────────────────────────

    @Test(
        description = "Checkbox should be present and visible on page load",
        groups = {"smoke", "dynamic"}
    )
    @Story("Checkbox State")
    @Severity(SeverityLevel.NORMAL)
    public void checkboxShouldBePresentOnLoad() {
        Assert.assertTrue(dynamicControlsPage.isCheckboxPresent(),
                "Checkbox should be present on page load.");
    }

    // ─── TC-DYN-02 ────────────────────────────────────────────────────────────

    @Test(
        description = "Clicking Remove should remove the checkbox from DOM and display a status message",
        groups = {"regression", "dynamic"}
    )
    @Story("Checkbox Removal")
    @Severity(SeverityLevel.CRITICAL)
    public void clickingRemove_shouldRemoveCheckboxAndShowMessage() {
        Assert.assertTrue(dynamicControlsPage.isCheckboxPresent(),
                "Checkbox must be present before removal.");

        dynamicControlsPage.clickToggleCheckbox();

        Assert.assertFalse(dynamicControlsPage.isCheckboxPresent(),
                "Checkbox should be removed from DOM after clicking Remove.");
        Assert.assertTrue(dynamicControlsPage.isStatusMessageDisplayed(),
                "Status message should appear after checkbox removal.");

        String msg = dynamicControlsPage.getStatusMessage();
        Assert.assertTrue(msg.toLowerCase().contains("gone"),
                "Status message should confirm checkbox is gone. Actual: " + msg);
    }

    // ─── TC-DYN-03 ────────────────────────────────────────────────────────────

    @Test(
        description = "After removing, clicking Add should re-add the checkbox and show a status message",
        groups = {"regression", "dynamic"}
    )
    @Story("Checkbox Re-addition")
    @Severity(SeverityLevel.CRITICAL)
    public void afterRemove_clickingAdd_shouldRestoreCheckbox() {
        // Step 1: Remove
        dynamicControlsPage.clickToggleCheckbox();
        Assert.assertFalse(dynamicControlsPage.isCheckboxPresent(),
                "Checkbox should be absent after Remove click.");

        // Step 2: Add back
        dynamicControlsPage.clickToggleCheckbox();
        Assert.assertTrue(dynamicControlsPage.isCheckboxPresent(),
                "Checkbox should be restored after clicking Add.");

        String msg = dynamicControlsPage.getStatusMessage();
        Assert.assertTrue(msg.toLowerCase().contains("back"),
                "Status message should confirm checkbox is back. Actual: " + msg);
    }

    // ─── TC-DYN-04 ────────────────────────────────────────────────────────────

    @Test(
        description = "Text input should be disabled on page load",
        groups = {"smoke", "dynamic"}
    )
    @Story("Input Enable/Disable")
    @Severity(SeverityLevel.NORMAL)
    public void textInputShouldBeDisabledOnLoad() {
        Assert.assertFalse(dynamicControlsPage.isTextInputEnabled(),
                "Text input should be disabled on initial page load.");
    }

    // ─── TC-DYN-05 ────────────────────────────────────────────────────────────

    @Test(
        description = "Clicking Enable should enable the text input and show a status message",
        groups = {"regression", "dynamic"}
    )
    @Story("Input Enable")
    @Severity(SeverityLevel.CRITICAL)
    public void clickingEnable_shouldEnableTextInput() {
        Assert.assertFalse(dynamicControlsPage.isTextInputEnabled(),
                "Input should be disabled before enabling.");

        dynamicControlsPage.clickToggleInput().waitForInputEnabled();

        Assert.assertTrue(dynamicControlsPage.isTextInputEnabled(),
                "Text input should be enabled after clicking Enable button.");

        String msg = dynamicControlsPage.getStatusMessage();
        Assert.assertTrue(msg.toLowerCase().contains("enabled"),
                "Status message should confirm input is enabled. Actual: " + msg);
    }

    // ─── TC-DYN-06 ────────────────────────────────────────────────────────────

    @Test(
        description = "After enabling, clicking Disable should disable the text input again",
        groups = {"regression", "dynamic"}
    )
    @Story("Input Disable")
    @Severity(SeverityLevel.CRITICAL)
    public void afterEnable_clickingDisable_shouldDisableTextInput() {
        // Step 1: Enable
        dynamicControlsPage.clickToggleInput().waitForInputEnabled();
        Assert.assertTrue(dynamicControlsPage.isTextInputEnabled(),
                "Input should be enabled before testing disable.");

        // Step 2: Type into it (confirms it's interactive)
        dynamicControlsPage.typeInTextInput("QA Automation");

        // Step 3: Disable
        dynamicControlsPage.clickToggleInput().waitForInputDisabled();
        Assert.assertFalse(dynamicControlsPage.isTextInputEnabled(),
                "Text input should be disabled after clicking Disable.");

        String msg = dynamicControlsPage.getStatusMessage();
        Assert.assertTrue(msg.toLowerCase().contains("disabled"),
                "Status message should confirm input is disabled. Actual: " + msg);
    }
}