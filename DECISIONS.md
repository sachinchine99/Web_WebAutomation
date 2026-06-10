# DECISIONS.md — Framework Design Decisions

> Treat this like a pull request description. Every decision here is one a new team member should understand on day one.

---

## 1. Language & Framework Choice

**Java 17 + Selenium WebDriver 4 + TestNG**

- **Java** is the industry standard for enterprise QA automation at scale. Strong typing catches contract violations at compile time — a meaningful advantage over dynamically typed alternatives when maintaining large suites.
- **Selenium 4** ships with W3C WebDriver Protocol compliance, built-in BiDi support, and improved relative locators. WebDriverManager removes all manual driver binary management.
- **TestNG** over JUnit 5 for this project because: native parallel execution control at the suite/class/method level, built-in `IRetryAnalyzer`, data-provider support, and the `testng.xml` suite file gives granular group-based execution — all of which are standard requirements on real QA teams.

---

## 2. Design Pattern — Page Object Model

All locators and interactions live exclusively in Page Object classes. Test classes contain zero `By` locators and zero `WebElement` references.

**Why this boundary matters:**  
When a locator changes (it always does), you change it in exactly one place — the page class. If locators leak into tests, a single UI change can break dozens of test files.

`BasePage` provides the abstraction layer: `click()`, `type()`, `getText()` etc. are wrapped with logging and waits. Raw `driver.findElement()` is never called from tests.

---

## 3. ThreadLocal WebDriver — Parallel Safety

`DriverManager` uses `ThreadLocal<WebDriver>` to store one driver instance per thread.

This means:
- No `static WebDriver driver` shared across threads
- No race conditions when `thread-count > 1` in `testng.xml`
- Driver lifecycle (`initDriver` / `quitDriver`) is cleanly managed in `BaseTest`

Increasing `thread-count` in `testng.xml` scales parallel execution without any code changes.

---

## 4. No Thread.sleep() — Explicit Waits Only

`Thread.sleep()` is banned from the codebase. It causes two problems: flakiness (too short) and unnecessary slowness (too long).

`WaitUtility` provides a single, central wait library used by all page objects. Every method uses `WebDriverWait` with polling intervals and targeted `ExpectedConditions`.

For the Dynamic Controls page specifically: after every button click, the test waits for the `#message` element to appear — this is the true DOM signal that the async operation completed, not a time guess.

---

## 5. Configuration Strategy

`ConfigReader` is a singleton that loads `config.properties` once.

**Environment variable override:** Any config key can be overridden by a matching environment variable (key uppercased, dots replaced by underscores). This means:
- Local dev uses the properties file
- CI injects secrets via GitHub Actions `env` — no credentials committed to source control

This pattern is used in production CI systems at most mature engineering orgs.

---

## 6. Dual Reporting — Extent + Allure

- **Extent Reports**: Rich, self-contained HTML file. Ideal for sharing with non-technical stakeholders — no server needed, open in any browser.
- **Allure**: Structured JSON output that integrates with CI dashboards (GitHub Actions, Jenkins, GitLab CI). Supports trend graphs across runs, categories, and severity breakdown.

Both are generated in the same run. `TestListener` attaches screenshots to both on failure.

---

## 7. Screenshot on Failure

`TestListener.onTestFailure()` captures a screenshot and:
1. Saves it to disk (path embedded in the Extent HTML report)
2. Attaches it as bytes to Allure (visible inline in the report)

This happens automatically for every failing test — no test-level boilerplate needed.

---

## 8. Retry Mechanism

`RetryAnalyzer` retries a failed test once (configurable via `retry.count`).

The default is intentionally low. Retries mask genuine bugs if overused. The intent here is to tolerate one-off network blips or rendering delays on the herokuapp.com demo environment — not to hide real failures. On a stable internal staging environment, `retry.count=0` is preferable.

---

## 9. File Upload Strategy

File uploads use `fileInput.sendKeys(absolutePath)` — no `Robot`, no AutoIt, no OS dialog interaction.

This works because `<input type="file">` is a standard HTML element that Selenium can interact with directly. Benefits:
- Works headless (no display required)
- Works on Linux CI runners with no GUI
- Faster and more reliable than simulating OS dialogs

The test file lives in `src/test/resources/testdata/` and is resolved via `ClassLoader.getResource()` — path is always correct regardless of OS or working directory.

---

## 10. What I Would Add with More Time

- **API + UI contract tests**: After a login API call returns a session token, assert the UI reflects the same authenticated state — crossing the layer boundary to catch integration gaps.
- **Cucumber BDD layer**: For scenarios that need business-readable specs, I would add a Gherkin layer on top of the existing page objects. The POM is already structured to support this with no refactoring.
- **Docker Selenium Grid**: Replace local WebDriverManager with a Selenium Grid container in `docker-compose.yml` for fully portable, infrastructure-agnostic execution.
- **Visual regression**: Integrate Percy or Applitools for pixel-level diff on key pages — especially useful for the checkout UI rebuild scenario described in the assignment.
- **Database validation layer**: For end-to-end order tests, assert the DB state (via JDBC) matches what the UI shows — verifying the full system, not just the surface.