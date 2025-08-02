package com.framework.pages;

import com.framework.base.BasePage;
import com.framework.utils.LoggerUtil;
import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SelectTravellersPage extends BasePage {
    private final String widgetXpath = "//div[@class=\"railTravellersWrapper\"]";
    private final String payAndBookNowWraper = widgetXpath + "//div[@class=\"payNowWrapper\"]";

    public SelectTravellersPage(WebDriver driver) {
        super(driver);
        this.waitUntilVisible(By.xpath(widgetXpath), "SelectTravellersPage Widget");
    }

    public AddTravellerInformationDialog clickAddTraveller() {
        Allure.step("Click add Traveller to open 'Add Traveller Information' dialog", () -> {
            this.clickElement(By.xpath(widgetXpath + "//span[text()=\"Add Traveller\"]"), "Add Traveller");
        });
        return new AddTravellerInformationDialog(this.driver);
    }

    public void clickPayAndBookNow() {
        String log = "Click 'Pay & Book Now'";
        Allure.step(log, () -> {
            this.clickElement(By.xpath(payAndBookNowWraper + "//span[contains(., \"Pay & Book Now\")]"), log);
            String errMsg = this.getElementText(By.xpath(widgetXpath + "//h3[text()=\"IRCTC Account Details\"]//following-sibling::div//p[contains(@class, \"errorMsg\")]"), "Error message");
            LoggerUtil.attachScreenshot("Error message displayed is: " + errMsg);
        });
    }

    public void printAllDetailsFromPayAndBookNowWidget() {
        String log = "Print All Details From Pay And Book Now Widget";
        Allure.step(log, () -> {
            String details = this.getElementText(By.xpath(payAndBookNowWraper + "//div[@class=\"paymentDetailsWrapper\"]"), "payment Details Wrapper");
            Allure.step("Details are: " + details);
        });
    }

}