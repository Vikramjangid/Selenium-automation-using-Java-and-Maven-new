package com.framework.tests;

import com.framework.base.DriverFactory;
import com.framework.pages.DashboardPage;
import com.framework.utils.ConfigReader;
import com.framework.utils.LoggerUtil;
import io.qameta.allure.*;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Epic("MakeMyTrip Booking")
@Feature("Train Booking")
@Listeners({io.qameta.allure.testng.AllureTestNg.class})
public class TrainBookingTest {
    private WebDriver driver;
    private DashboardPage dashboardPage;

    @BeforeClass
    public void setUp() {
        LoggerUtil.step("==== [TrainBookingTest] Test Initialization Started ====");
        driver = DriverFactory.initDriver();

        String url = ConfigReader.getProperty("baseUrl");
        String log = "[Navigation] Opening application URL: " + url;
        Allure.step(log, () -> {
            LoggerUtil.step(log);
            driver.get(url);
            LoggerUtil.step("[Navigation] Successfully navigated to: " + url);
        });

        dashboardPage = new DashboardPage(driver);
    }

    @Test(description = "Book a train ticket from Vadodara to Surat")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Search and try to book the first train after 9 PM on Friday")
    public void testTrainBooking() {
        String expectedTitle = "MakeMyTrip - #1 Travel Website 50% OFF on Hotels, Flights & Holiday";
        String actualTitle = dashboardPage.getPageTitle();

        LoggerUtil.compare(actualTitle, expectedTitle, "Verify Page title");

//        LoggerUtil.compare(0, 1, "comparison of 0 and 1");
    }

    @AfterClass
    public void tearDown() {
        LoggerUtil.step("==== [TrainBookingTest] Test Execution Completed ====");
        DriverFactory.quitDriver();
        LoggerUtil.softAssert.assertAll();
    }
}
