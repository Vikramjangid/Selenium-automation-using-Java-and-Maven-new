package com.framework.pages;

import com.framework.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ConfirmedOptionsDialog extends BasePage {
    private final String widgetXpath = "//div[contains(@class, \"Modal\") and contains(., \"Confirmed Options\")]";

    public ConfirmedOptionsDialog(WebDriver driver) {
        super(driver);
        this.waitForTimeout(1, "Wait for Confirmed Option dialog to load");
    }

    public boolean checkIfConfirmedOptionsDialogExists() {
        return this.isElementVisible(By.xpath(widgetXpath), "Confirmed Options dialog");
    }

    public void clickBookNowButton() {
        String contentArea = widgetXpath + "//div[contains(@class, \"Modal_modalContent\")]";
        this.clickElement(By.xpath(contentArea + "//span[contains(., \"Book Now\")]"), "Book Now button");
    }
}