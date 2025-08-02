package com.framework.pages;

import com.framework.base.BasePage;
import com.framework.utils.DateUtil;
import com.framework.utils.LoggerUtil;
import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class TrainsPage extends BasePage {

    public TrainsPage(WebDriver driver) {
        super(driver);
        this.waitUntilExpectedUrl("https://www.makemytrip.com/railways/");
    }

    public void clickBookTrainTickets() {
        this.clickElement(By.xpath("//span[contains(., \"Book Train Tickets\")]"), "Book Train Tickets checkbox");
    }

    public void fillTravelClass(String travelClass) {
        Allure.step("Enter travel class: " + travelClass, () -> {
            this.clickElement(By.xpath("//label[@for=\"travelClass\"]"), "travelClass label to activate dropdown");
            this.clickElement(By.xpath("//ul[@class=\"travelForPopup\"]//li[contains(., \"First AC\")]"), "Select value from dropdown");
        });
    }

    public void fillFromCity(String location) {
        Allure.step("Enter source city: " + location, () -> {
            this.clickElement(By.xpath("//label[@for=\"fromCity\"]"), "fromCity label to activate dropdown");
            this.selectLocationFromCustomDropdown(location);
        });
    }

    public void fillToCity(String location) {
        Allure.step("Enter destination city: " + location, () -> {
            this.clickElement(By.xpath("//label[@for=\"toCity\"]"), "toCity label to activate dropdown");
            this.selectLocationFromCustomDropdown(location);
        });
    }

    public void selectLocationFromCustomDropdown(String location) {
        String customDropdownWidgetXpath = "//div[contains(@class, \"autoSuggestPlugin\")]";
        this.typeText(By.xpath(customDropdownWidgetXpath + "//input"), location, "Location dropdown");
        this.waitForTimeout(1, "Wait for custom dropdown list to load with searched data");
        this.clickElement(By.xpath(customDropdownWidgetXpath + "//li[contains(., \"" + location + "\")]"), "Select value from list");
//        this.clickBookTrainTickets();
//        this.waitForElementToDisappear(By.xpath(customDropdownWidgetXpath));
    }

    public void fillNextFridayDate() {
        Allure.step("Select next friday date from calender widget", () -> {
            this.clickElement(By.xpath("//label[@for=\"travelDate\"]"), "Travel Date label to activate calendar");
            this.selectDateFromCustomPicker(DateUtil.getNextFriday());
        });
    }

    public void clickSearchButton() {
        this.clickElement(By.xpath("//a[@data-cy=\"submit\" and contains(., \"Search\")]"), "Search button");
    }

    public SelectTravellersPage searchTrainForNextFriday(String from, String to, String travelClass) {
        AtomicReference<WebElement> elementToClick = new AtomicReference<>();
        Allure.step("Search train for next Friday with destination: " + to + " from source: " + from + " With travel class:" + travelClass, () -> {
            this.fillFromCity(from);
            this.fillTravelClass(travelClass);
            this.fillToCity(to);
            this.fillNextFridayDate();
            this.clickSearchButton();

            this.clickElement(By.xpath("//div[contains(@class, \"FilterCard_filterCardSection\") and contains(., \"Departure from " + from + "\")]//li[@data-testid=\"filter-option-6pm - 12am\"]"), "Departure from " + from + " filter-option-6pm - 12am");
            this.waitForTimeout(1, "Wait for data to be filtered");

            Allure.step("Fetch all available train details", () -> {
                elementToClick.set(this.getAvailableTrainDetails());
            });
        });

        Allure.step("Click on first train after 6 PM", () -> {
            elementToClick.get().click();
        });
        return new SelectTravellersPage(this.driver);
    }

    public WebElement getAvailableTrainDetails() {
        WebElement elementToClick = null;
        String listingCardXpath = "//div[contains(@class, \"ListingCard_listingTopInfo\")]";
        this.waitUntilVisible(By.xpath(listingCardXpath), "Each train details");
        List<WebElement> allTrainsListingCardElements = this.getElements(By.xpath(listingCardXpath));

        for (WebElement listingCard : allTrainsListingCardElements) {
            String trainName = listingCard.findElement(By.xpath(".//p[@data-testid=\"train-name\"]")).getText();
            String dateTimeInfo = listingCard.findElement(By.xpath(".//div[contains(@class, \"ListingCard_dateTimeInfo\")]")).getText();
            LoggerUtil.step("--------------------------------------------------------------------------------------------------------------");
            LoggerUtil.step("Train details: Train Name=" + trainName + " | Date Time Info=" + dateTimeInfo);

            List<WebElement> seatInfoElements = listingCard.findElements(By.xpath(".//div[@data-testid=\"card-wrapper\" and contains(., \"Available\")]"));

            if (elementToClick == null) {
                // set element to click on first train after 6 PM
                elementToClick = seatInfoElements.get(0);
            }

            for (WebElement seatInfoElement : seatInfoElements) {
                String classInfo = seatInfoElement.findElement(By.xpath(".//p[@data-testid=\"class-info\"]")).getText();
                String availabilityDetails = seatInfoElement.findElement(By.xpath(".//p[@data-testid=\"availability-text\"]")).getText();
                String priceDetails = seatInfoElement.findElement(By.xpath(".//p[contains(@class, \"Cards_totalText\")]")).getText();
                LoggerUtil.step("-----------------");
                LoggerUtil.step("classInfo=" + classInfo + " | AvailabilityDetails=" + availabilityDetails + " | PriceDetails=" + priceDetails);
            }
        }
        return elementToClick;
    }
}
