package com.framework.pages;

import com.framework.base.BasePage;
import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class AddTravellerInformationDialog extends BasePage {
    private final String widgetXpath = "//div[@id=\"mmt-rails-add-traveller\"]";

    public AddTravellerInformationDialog(WebDriver driver) {
        super(driver);
        this.waitUntilVisible(By.xpath(widgetXpath), "Add Traveller Information Dialog Widget");
    }

    public void fillName(String name) {
        this.typeText(By.xpath(widgetXpath + "//input[@id=\"name\"]"), name, "Enter name");
    }

    public void fillAge(String age) {
        this.typeText(By.xpath(widgetXpath + "//input[@id=\"age\"]"), age, "Enter Age (in years)");
    }

    public void selectGender(String gender) {
        String genderDropdownWidgetXpath = widgetXpath + "//label[@for=\"gender\"]//following-sibling::div";
        this.clickElement(By.xpath(genderDropdownWidgetXpath), "Click gender widget to activate dropdown");
        this.clickElement(By.xpath(genderDropdownWidgetXpath + "//li[contains(., \"" + gender + "\")]"), "Dropdown list element for gender: " + gender);
    }

    public void clickAddButton() {
        this.clickElement(By.xpath(widgetXpath + "//button[contains(., \"Add\")]"), "Add button");
    }

    public void addTravellerInformation(String name, String age, String gender) {
        Allure.step("Add new traveller with name = " + name + " age = " + age + " gender = " + gender, () -> {
            this.fillName(name);
            this.fillAge(age);
            this.selectGender(gender);
            this.clickAddButton();
        });
    }
}