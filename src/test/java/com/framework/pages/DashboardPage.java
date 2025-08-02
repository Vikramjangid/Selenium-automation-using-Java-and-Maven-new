package com.framework.pages;

import com.framework.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DashboardPage extends BasePage {

    private final String navigationBarXpath = "//nav";

    public DashboardPage(WebDriver driver) {
        super(driver);
        clickElement(By.xpath("//span[@data-cy='closeModal']"), "Cross Button of 'Login/Signup With' dialog");
        waitUntilVisible(By.xpath(navigationBarXpath), "Dashboard navigation menu bar");
    }

    public void navigateTo(String menuItemName) {
        clickElement(By.xpath(navigationBarXpath + "//ul//li[@data-cy=\"menu_" + menuItemName + "\"]"), "Navigation menu button for " + menuItemName);
    }

    public TrainsPage goToTrainsPage() {
        this.navigateTo("Trains");
        return new TrainsPage(this.driver);
    }
}
