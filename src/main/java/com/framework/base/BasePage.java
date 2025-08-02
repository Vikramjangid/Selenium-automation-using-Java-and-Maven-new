package com.framework.base;

import com.framework.utils.LoggerUtil;
import io.qameta.allure.Allure;
import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class BasePage {
    private static final int DEFAULT_TIMEOUT = 20;
    protected WebDriver driver;
    protected WebDriverWait wait;

    // Constructor: Initializes WebDriver, WebDriverWait, and PageFactory elements
    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
        PageFactory.initElements(driver, this);
    }

    // Returns the current page title
    public String getPageTitle() {
        String title = driver.getTitle();
        LoggerUtil.step("Page title retrieved: " + title);
        return title;
    }

    // Waits for visibility and returns a single WebElement by locator
    protected WebElement getElement(By locator, String description) {
        LoggerUtil.debug("Waiting for visibility of element: " + description);
        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            LoggerUtil.step("Element found: " + description);
            return element;
        } catch (TimeoutException e) {
            LoggerUtil.error("Timeout: Element not found - " + description);
            throw e;
        }
    }

    // Returns a list of WebElements matching the locator
    public List<WebElement> getElements(By locator) {
        try {
            List<WebElement> elements = driver.findElements(locator);
            if (elements.isEmpty()) {
                LoggerUtil.debug("No elements found for locator: " + locator.toString());
            } else {
                LoggerUtil.debug("Found " + elements.size() + " elements for locator: " + locator.toString());
            }
            return elements;
        } catch (Exception e) {
            LoggerUtil.error("Error while locating elements: " + locator.toString() + " - " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Clicks on the element found by locator, logs the action, and adds Allure step
    public void clickElement(By locator, String description) {
        String log = "Click on: " + description;
        Allure.step(log, () -> {
            LoggerUtil.step(log);
            WebElement element = getElement(locator, description);
            element.click();
        });
    }

    // Types text into the element found by locator, logs the action, and adds Allure step
    public void typeText(By locator, String text, String description) {
        String log = "Type text into: " + description + " | Text: " + text;
        Allure.step(log, () -> {
            LoggerUtil.step(log);
            WebElement element = getElement(locator, description);
            element.clear();
            element.sendKeys(text);
        });
    }

    // Returns the text of the element found by locator
    public String getElementText(By locator, String description) {
        LoggerUtil.step("Getting text from: " + description);
        WebElement element = getElement(locator, description);
        return element.getText();
    }

    // Selects an option in a dropdown by visible text
    public void selectByVisibleText(By locator, String visibleText, String description) {
        LoggerUtil.step("Selecting from dropdown by visible text: " + visibleText + " on " + description);
        WebElement dropdown = getElement(locator, description);
        new Select(dropdown).selectByVisibleText(visibleText);
    }

    // Selects an option in a dropdown by value
    public void selectByValue(By locator, String value, String description) {
        LoggerUtil.step("Selecting from dropdown by value: " + value + " on " + description);
        WebElement dropdown = getElement(locator, description);
        new Select(dropdown).selectByValue(value);
    }

    // Selects an option in a dropdown by index
    public void selectByIndex(By locator, int index, String description) {
        LoggerUtil.step("Selecting from dropdown by index: " + index + " on " + description);
        WebElement dropdown = getElement(locator, description);
        new Select(dropdown).selectByIndex(index);
    }

    // Checks if the element is visible on the page
    public boolean isElementVisible(By locator, String description) {
        LoggerUtil.debug("Checking visibility of: " + description);
        try {
            return getElement(locator, description).isDisplayed();
        } catch (NoSuchElementException | TimeoutException e) {
            LoggerUtil.debug("Element not visible: " + description);
            return false;
        }
    }

    // Waits until the element is visible and returns it, or null if not found
    public WebElement waitUntilVisible(By locator, String elementName) {
        try {
            LoggerUtil.debug("Waiting for visibility of element: " + elementName);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            LoggerUtil.step("Element visible: " + elementName);
            return element;
        } catch (TimeoutException e) {
            LoggerUtil.fail("Element not visible after 10 seconds: " + elementName);
        } catch (Exception ex) {
            LoggerUtil.error("Exception while waiting for visibility of element: " + elementName + ". Error: " + ex.getMessage());
        }
        return null;
    }

    // Waits until the current URL matches the expected URL
    public void waitUntilExpectedUrl(String expectedUrl) {
        LoggerUtil.step("Waiting until URL becomes: " + expectedUrl);
        for (int i = 0; i < 10; i++) {
            String currentUrl = driver.getCurrentUrl();
            LoggerUtil.debug("Attempt " + (i + 1) + ": Current URL - " + currentUrl);
            if (currentUrl.equals(expectedUrl)) {
                LoggerUtil.pass("URL matched expected: " + expectedUrl);
                return;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                LoggerUtil.error("Thread interrupted while waiting for URL: " + e.getMessage());
            }
        }
        LoggerUtil.fail("URL did not match expected value: " + expectedUrl);
    }

    // Waits for the element to disappear from the page
    public void waitForElementToDisappear(By locator) {
        int timeoutInSeconds = 5;
        try {
            LoggerUtil.debug("Waiting for element to disappear: " + locator.toString());
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            boolean disappeared = wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
            if (disappeared) {
                LoggerUtil.step("Element disappeared successfully: " + locator);
            } else {
                LoggerUtil.fail("Element did not disappear in expected time: " + locator);
            }
        } catch (TimeoutException e) {
            LoggerUtil.fail("Timeout: Element did not disappear within " + timeoutInSeconds + " seconds: " + locator);
        } catch (Exception e) {
            LoggerUtil.error("Error while waiting for element to disappear: " + e.getMessage());
        }
    }

    // Waits for a specified number of seconds (thread sleep)
    public void waitForTimeout(int seconds, String reason) {
        LoggerUtil.step("Waiting for " + seconds + " second(s). Reason: " + reason);
        try {
            Thread.sleep(seconds * 1000L);
            LoggerUtil.debug("Waited for " + seconds + " second(s) successfully.");
        } catch (InterruptedException e) {
            LoggerUtil.error("Interrupted during wait: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    // Selects a date from a custom date picker widget
    public void selectDateFromCustomPicker(LocalDate targetDate) {
        String expectedMonthYear = targetDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + targetDate.getYear();

        LoggerUtil.step("Selecting date: " + targetDate);

        // Wait for calendar to be visible
        WebElement monthCaption = this.waitUntilVisible(By.xpath("//div[@class=\"DayPicker-Caption\"]/div"), "Month and year");

        // Navigate to correct month
        for (int i = 0; i < 12; i++) {
            String visibleMonth = monthCaption.getText().trim();

            if (visibleMonth.equalsIgnoreCase(expectedMonthYear)) {
                break;
            }

            WebElement nextButton = driver.findElement(By.xpath("//span[@aria-label=\"Next Month\"]"));
            if (nextButton.isEnabled()) {
                nextButton.click();
                wait.until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(monthCaption, visibleMonth)));
            } else {
                LoggerUtil.fail("Next button disabled. Cannot reach the target month: " + expectedMonthYear);
                return;
            }
        }

        // Build aria-label for target date, e.g., "Tue Aug 05 2025"
        String targetAriaLabel = targetDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                + " " + targetDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                + " " + String.format("%02d", targetDate.getDayOfMonth())
                + " " + targetDate.getYear();

        List<WebElement> dateElements = driver.findElements(By.xpath("//div[contains(@class, \"DayPicker-Day\") and @aria-disabled=\"false\"]"));
        boolean dateFound = false;

        for (WebElement day : dateElements) {
            String ariaLabel = day.getAttribute("aria-label");
            if (ariaLabel != null && ariaLabel.equals(targetAriaLabel)) {
                day.click();
                LoggerUtil.pass("Successfully selected date: " + ariaLabel);
                dateFound = true;
                break;
            }
        }

        if (!dateFound) {
            LoggerUtil.fail("Could not find the expected date: " + targetAriaLabel);
        }
    }
}
