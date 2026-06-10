package utilities;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import java.time.Duration;

/**
 * ThreadLocal WebDriver manager — guarantees one driver instance per thread,
 * enabling safe parallel execution without static state collisions.
 */
public final class DriverManager {

    private static final Logger log = LogManager.getLogger(DriverManager.class);
    private static final ThreadLocal<WebDriver> driverThread = new ThreadLocal<>();
    private static final ConfigReader config = ConfigReader.getInstance();

    private DriverManager() {}

    public static void initDriver() {
        String browser = System.getProperty("browser", config.getBrowser()).toLowerCase();
        boolean headless = Boolean.parseBoolean(System.getProperty("headless",
                String.valueOf(config.isHeadless())));

        log.info("Initialising browser: {} | headless: {}", browser, headless);

        WebDriver driver = switch (browser) {
            case "edge" -> createEdgeDriver(headless);
            default     -> createChromeDriver(headless);
        };

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(config.getPageLoadTimeout()));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0)); // Explicit waits only
        driver.manage().window().maximize();

        driverThread.set(driver);
        log.info("WebDriver initialised successfully on thread: {}", Thread.currentThread().getName());
    }

    public static WebDriver getDriver() {
        WebDriver driver = driverThread.get();
        if (driver == null) {
            throw new IllegalStateException("WebDriver not initialised for thread: "
                    + Thread.currentThread().getName());
        }
        return driver;
    }

    public static void quitDriver() {
        WebDriver driver = driverThread.get();
        if (driver != null) {
            log.info("Quitting WebDriver on thread: {}", Thread.currentThread().getName());
            driver.quit();
            driverThread.remove();
        }
    }

    // ─── Browser factories ────────────────────────────────────────────────────

    private static WebDriver createChromeDriver(boolean headless) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions opts = new ChromeOptions();
        applyCommonFlags(opts, headless);
        opts.addArguments("--remote-allow-origins=*");
        return new ChromeDriver(opts);
    }

    private static WebDriver createEdgeDriver(boolean headless) {
        WebDriverManager.edgedriver().setup();
        EdgeOptions opts = new EdgeOptions();
        applyCommonFlags(opts, headless);
        return new EdgeDriver(opts);
    }

    private static void applyCommonFlags(ChromeOptions opts, boolean headless) {
        if (headless) opts.addArguments("--headless=new");
        opts.addArguments(
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-gpu",
                "--window-size=1920,1080",
                "--disable-extensions",
                "--disable-infobars"
        );
    }

    private static void applyCommonFlags(EdgeOptions opts, boolean headless) {
        if (headless) opts.addArguments("--headless=new");
        opts.addArguments(
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-gpu",
                "--window-size=1920,1080"
        );
    }
}