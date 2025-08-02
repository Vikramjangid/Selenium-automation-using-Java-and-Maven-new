package com.framework.utils;

import com.framework.base.DriverFactory;
import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggerUtil {
    private static final Logger logger = LogManager.getLogger(LoggerUtil.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static SoftAssert softAssert = new SoftAssert();

    // Returns the current timestamp as a formatted string
    private static String getTimeStamp() {
        return formatter.format(LocalDateTime.now());
    }

    // Logs a message with the specified log level and adds Allure steps
    private static void logWithFormat(String level, String message) {
        // String formatted = String.format("%s [%s] %s", getTimeStamp(), level.toUpperCase(), message);
        // INFO: disable below line and enable above line to get logs with time stamp and log type
        String formatted = message;
        switch (level.toLowerCase()) {
            case "debug":
                logger.debug(formatted);
                break;
            case "info":
                logger.info(formatted);
                break;
            case "pass":
                logger.info(formatted); // For custom level
                break;
            case "fail":
            case "error":
                logger.error(formatted);
                break;
            default:
                logger.info(formatted);
        }
        if (level.equalsIgnoreCase("fail") || level.equalsIgnoreCase("error")) {
            Allure.step(message, Status.FAILED);
        }
        Allure.step(message);
    }

    // Logs a step/info message
    public static void step(String message) {
        logWithFormat("info", message);
    }

    // Logs an info message
    public static void log(String message) {
        logWithFormat("info", message);
    }

    // Logs a debug message
    public static void debug(String message) {
        logWithFormat("debug", message);
    }

    // Logs a pass/success message
    public static void pass(String message) {
        logWithFormat("pass", "✅ " + message);
    }

    // Logs a failure message, marks soft assertion as failed, and attaches a screenshot
    public static void fail(String message) {
        String log = "❌ " + message;
        logWithFormat("fail", log);
        softAssert.fail(log);
        attachScreenshot("Failure Screenshot: " + log);
    }

    // Logs an error message, attaches a screenshot, and fails the test
    public static void error(String message) {
        String log = "❌ " + message;
        logWithFormat("error", log);
        attachScreenshot("Error Screenshot: " + log);
        Assert.fail(log);
    }

    // Compares actual and expected values, logs result as pass or fail
    public static void compare(Object actual, Object expected, String message) {
        message = "[Validation] " + message;
        if (actual == null && expected == null) {
            pass(message + " - Actual and Expected both are null.");
        } else if (actual != null && actual.equals(expected)) {
            pass(message + " - Comparison passed. Expected: " + expected + ", Actual: " + actual);
        } else {
            fail(message + " - Comparison failed. Expected: " + expected + ", Actual: " + actual);
        }
    }

    // Captures and attaches a screenshot to the Allure report
    public static void attachScreenshot(String name) {
        try {
            WebDriver driver = DriverFactory.getDriver();
            if (driver != null) {
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                Allure.addAttachment(name, new ByteArrayInputStream(screenshot));
            }
        } catch (Exception e) {
            logger.error(getTimeStamp() + " [ERROR] Failed to capture screenshot: " + e.getMessage());
        }
    }
}
