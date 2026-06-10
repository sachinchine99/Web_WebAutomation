package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import base.BasePage;
import utilities.ConfigReader;
import utilities.WaitUtility;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Page Object for: https://the-internet.herokuapp.com/upload
 *
 * File upload is handled by sending the absolute file path directly to the
 * hidden <input type="file"> element — no OS file dialog interaction needed,
 * which makes the test headless-compatible and CI-safe.
 */
public class FileUploadPage extends BasePage {

    private static final String PAGE_PATH = "/upload";

    // ─── Locators ─────────────────────────────────────────────────────────────

    @FindBy(id = "file-upload")
    private WebElement fileInput;

    @FindBy(id = "file-submit")
    private WebElement uploadButton;

    @FindBy(id = "uploaded-files")
    private WebElement uploadedFileName;

    @FindBy(css = "div.example h3")
    private WebElement uploadConfirmationHeader;

    // ─── Constructor ──────────────────────────────────────────────────────────

    public FileUploadPage(WebDriver driver) {
        super(driver);
    }

    // ─── Navigation ───────────────────────────────────────────────────────────

    public FileUploadPage open() {
        navigateTo(ConfigReader.getInstance().getBaseUrl() + PAGE_PATH);
        WaitUtility.waitForVisibility(driver, fileInput);
        log.info("FileUploadPage opened.");
        return this;
    }

    // ─── Actions ─────────────────────────────────────────────────────────────

    /**
     * Sends the absolute path of a classpath resource to the file input.
     * Works in headless mode — no OS dialog involvement.
     *
     * @param classpathResource relative path inside src/test/resources (e.g. "testdata/upload_file.txt")
     * @return this page for chaining
     */
    public FileUploadPage uploadFileFromClasspath(String classpathResource) {
        File file = resolveClasspathFile(classpathResource);
        log.info("Uploading file: {}", file.getAbsolutePath());
        // sendKeys on <input type="file"> bypasses the OS dialog entirely
        fileInput.sendKeys(file.getAbsolutePath());
        return this;
    }

    /**
     * Uploads a file at an explicit absolute path.
     */
    public FileUploadPage uploadFileFromPath(String absolutePath) {
        File file = new File(absolutePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("Upload file not found at path: " + absolutePath);
        }
        log.info("Uploading file from absolute path: {}", absolutePath);
        fileInput.sendKeys(absolutePath);
        return this;
    }

    public FileUploadPage clickUpload() {
        log.info("Clicking upload submit button");
        click(uploadButton);
        WaitUtility.waitForVisibility(driver, uploadedFileName);
        return this;
    }

    // ─── Assertion support ────────────────────────────────────────────────────

    public String getUploadedFileName() {
        return getText(uploadedFileName);
    }

    public String getConfirmationHeader() {
        return getText(uploadConfirmationHeader);
    }

    public boolean isUploadSuccessful() {
        return isDisplayed(uploadedFileName);
    }

    public boolean isConfirmationHeaderDisplayed() {
        return isDisplayed(uploadConfirmationHeader);
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    private File resolveClasspathFile(String resourcePath) {
        URL resourceUrl = getClass().getClassLoader().getResource(resourcePath);
        if (resourceUrl == null) {
            throw new IllegalArgumentException("Test resource not found on classpath: " + resourcePath);
        }
        try {
            return new File(resourceUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid URI for resource: " + resourcePath, e);
        }
    }
}