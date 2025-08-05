package com.framework.pages;

import com.framework.base.BasePage;
import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DashboardPage extends BasePage {

    private final String navigationBarXpath = "//nav";

    public DashboardPage(WebDriver driver) {
        super(driver);
        clickElement(By.xpath("//span[@data-cy='closeModal']"), "Cross Button of 'Login/Signup With' dialog");
        this.changeToIndiaWebsite();
        waitUntilVisible(By.xpath(navigationBarXpath), "Dashboard navigation menu bar");
    }

    public void changeToIndiaWebsite() {
        String countryLanguageSelectionXpath = "//span[@data-testid=\"country-lang-switcher\"]";
        String text = this.getElementText(By.xpath(countryLanguageSelectionXpath), "country-lang-switcher");
        if (text.contains("INR")) {
            return;
        }
        else {
            Allure.step("Change country and language to INR", () -> {
                this.clickElement(By.xpath(countryLanguageSelectionXpath), "country-lang-switcher");
                CountryLanguageDialog countryLanguageDialog = new CountryLanguageDialog(this.driver);
                countryLanguageDialog.changeCountryAndApply();
                clickElement(By.xpath("//span[@data-cy='closeModal']"), "Cross Button of 'Login/Signup With' dialog");
            });
        }
    }

    public void navigateTo(String menuItemName) {
        clickElement(By.xpath(navigationBarXpath + "//ul//li[@data-cy=\"menu_" + menuItemName + "\"]"), "Navigation menu button for " + menuItemName);
    }

    public TrainsPage goToTrainsPage() {
        this.navigateTo("Trains");
        return new TrainsPage(this.driver);
    }
}
