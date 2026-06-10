package tests;
import io.qameta.allure.*;
import pages.FileUploadPage;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import baseTest.BaseTest;

/**
 * FileUploadTest — validates the file upload feature on:
 * https://the-internet.herokuapp.com/upload
 *
 * Technique: sendKeys() to <input type="file"> — no OS dialog,
 * works cleanly in headless mode and CI pipelines.
 */
@Epic("ShopNest Checkout 2.0 — QA Assignment")
@Feature("File Upload")
public class FileUploadTest extends BaseTest {

    private static final String UPLOAD_RESOURCE = "testdata/upload_file.txt";
    private static final String EXPECTED_FILENAME = "upload_file.txt";

    private FileUploadPage fileUploadPage;

    @BeforeMethod(alwaysRun = true)
    public void openPage() {
        fileUploadPage = new FileUploadPage(getDriver()).open();
    }

    // ─── TC-UPLOAD-01 ─────────────────────────────────────────────────────────

    @Test(
        description = "Uploading a valid file should display a success confirmation header",
        groups = {"smoke", "upload"}
    )
    @Story("File Upload Success")
    @Severity(SeverityLevel.CRITICAL)
    public void uploadFile_shouldShowSuccessConfirmation() {
        fileUploadPage
                .uploadFileFromClasspath(UPLOAD_RESOURCE)
                .clickUpload();

        Assert.assertTrue(fileUploadPage.isUploadSuccessful(),
                "Upload confirmation element should be visible after a successful upload.");
    }

    // ─── TC-UPLOAD-02 ─────────────────────────────────────────────────────────

    @Test(
        description = "Uploaded filename should match the file selected for upload",
        groups = {"regression", "upload"}
    )
    @Story("Filename Verification")
    @Severity(SeverityLevel.CRITICAL)
    public void uploadFile_shouldDisplayCorrectFilename() {
        fileUploadPage
                .uploadFileFromClasspath(UPLOAD_RESOURCE)
                .clickUpload();

        String displayedName = fileUploadPage.getUploadedFileName();
        Assert.assertEquals(displayedName, EXPECTED_FILENAME,
                "Displayed filename should exactly match the uploaded file name.");
    }

    // ─── TC-UPLOAD-03 ─────────────────────────────────────────────────────────

    @Test(
        description = "Confirmation header should read 'File Uploaded!' after a successful upload",
        groups = {"regression", "upload"}
    )
    @Story("Confirmation Header")
    @Severity(SeverityLevel.NORMAL)
    public void uploadFile_shouldDisplayConfirmationHeader() {
        fileUploadPage
                .uploadFileFromClasspath(UPLOAD_RESOURCE)
                .clickUpload();

        String header = fileUploadPage.getConfirmationHeader();
        Assert.assertTrue(header.contains("File Uploaded"),
                "Confirmation header should contain 'File Uploaded'. Actual: " + header);
    }
}