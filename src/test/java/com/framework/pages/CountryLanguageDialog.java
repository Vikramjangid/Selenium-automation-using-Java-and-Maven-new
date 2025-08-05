package com.framework.pages;

import com.framework.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CountryLanguageDialog extends BasePage {
    private final String widgetXpath = "//div[contains(@class, \"GlobalPopup\")]";

    public CountryLanguageDialog(WebDriver driver) {
        super(driver);
        this.waitUntilVisible(By.xpath(widgetXpath), "Country Language Dialog");
    }

    public void changeCountryAndApply() {
        String originalWindow = driver.getWindowHandle();

        String dropDownXpath = widgetXpath + "//div[@data-testid=\"country-dropdown\"]";
        this.clickElement(By.xpath(dropDownXpath), "country dropdown");
        this.clickElement(By.xpath(dropDownXpath + "//p[@data-testid=\"IN-country\"]"), "Dropdown value for India");
        this.clickElement(By.xpath(widgetXpath + "//button[@data-testid=\"country-lang-submit\"]"), "Submit button");

        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(driver -> driver.getWindowHandles().size() > 1);

        for (String windowHandle : driver.getWindowHandles()) {
            if (!windowHandle.equals(originalWindow)) {
                driver.switchTo().window(windowHandle);
                break;
            }
        }
    }
}