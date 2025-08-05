package com.framework.pages;

import com.framework.base.BasePage;
import com.framework.utils.DateUtil;
import com.framework.utils.LoggerUtil;
import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class TrainsPage extends BasePage {

    public TrainsPage(WebDriver driver) {
        super(driver);
        this.waitUntilExpectedUrl("https://www.makemytrip.com/railways/");
    }

    public static void formatTrainInfo(String rawInfo) {
        String[] parts = rawInfo.split("\n");

        String departureTimeDate = parts[0].trim();
        String departureStation = parts[1].trim();
        String travelDuration = parts[2].trim();
        String arrivalTimeDate = parts[4].trim();  // skipping "View Route"
        String arrivalStation = parts[5].trim();

        LoggerUtil.step(String.format(
                "Departure time and date from '%s' is '%s' and Arrival date and time at '%s' is '%s' | Estimated time '%s'",
                departureStation, departureTimeDate, arrivalStation, arrivalTimeDate, travelDuration
        ));
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
    }

    public void fillNextFridayDate() {
        Allure.step("Select next friday date from calender widget", () -> {
            this.clickElement(By.xpath("//label[@for=\"travelDate\"]"), "Travel Date label to activate calendar");
            this.selectDateFromCustomPicker(DateUtil.getNextFriday());
        });
    }

    public void fillDate(LocalDate inputDate) {
        Allure.step("Select next friday date from calender widget", () -> {
            this.clickElement(By.xpath("//label[@for=\"travelDate\"]"), "Travel Date label to activate calendar");
            this.selectDateFromCustomPicker(inputDate);
        });
    }

    public void clickSearchButton() {
        this.clickElement(By.xpath("//a[@data-cy=\"submit\" and contains(., \"Search\")]"), "Search button");
    }

    public void applyDepartureFilter(String from, String filter) {
        Allure.step("Apply filter: Departure from " + from + " filter option " + filter, () -> {
            this.clickElement(By.xpath("//div[contains(@class, \"FilterCard_filterCardSection\") and contains(., \"Departure from " + from + "\")]//li[@data-testid=\"filter-option-" + filter + "\"]"), "Departure from " + from + " filter option " + filter);
            this.waitForTimeout(2, "Wait for data to be filtered");
        });
    }

    public void applyFilterForTravelClass() {
        String travelClass = this.getElementValue(By.xpath("//input[@id=\"travelFor\"]"), "Travel for class value input field");
        String[] parts = travelClass.split(",", 2); // Limit to 2 parts
        String input = parts.length > 1 ? parts[1].trim() : "";
        String log = "Apply filter for travel class:" + input;

        Allure.step(log, () -> {
            this.clickElement(By.xpath("//div[contains(@class, \"FilterCard\") and contains(., \"Journey Class Filters\")]//li[contains(., \"" + input + "\")]"), log);
            this.waitForTimeout(2, "Wait for data to be filtered");
        });
    }

    public SelectTravellersPage searchTrainForNextFriday(String from, String to, String travelClass, LocalDate inputDate) {
        AtomicReference<WebElement> elementToClick = new AtomicReference<>();
        Allure.step("Search train for next Friday with destination: " + to + " from source: " + from + " With travel class:" + travelClass, () -> {
            this.fillFromCity(from);
            this.fillTravelClass(travelClass);
            this.fillToCity(to);
            this.fillDate(inputDate);
            // disabled below line to enable custom date input using config
            // this.fillNextFridayDate();
            this.clickSearchButton();

            this.applyDepartureFilter(from, "6pm - 12am");
            this.applyFilterForTravelClass();

            Allure.step("Fetch all available train details", () -> {
                elementToClick.set(this.getAvailableTrainDetails());
            });
        });

        if (elementToClick.get() == null) {
            LoggerUtil.error("No seats are available on date with specified conditions. Please select a different date");
        }
        Allure.step("Click on first train after 6 PM", () -> {
            elementToClick.get().click();
        });

        ConfirmedOptionsDialog confirmedOptionsDialog = new ConfirmedOptionsDialog(this.driver);
        if (confirmedOptionsDialog.checkIfConfirmedOptionsDialogExists()) {
            confirmedOptionsDialog.clickBookNowButton();
        }

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

            List<WebElement> seatInfoElements = listingCard.findElements(By.xpath(".//div[@data-testid=\"card-wrapper\" and contains(., \"Available\")]"));

            if (elementToClick == null && !seatInfoElements.isEmpty()) {
                // set element to click on first train after 6 PM
                elementToClick = seatInfoElements.get(0);
            }

            Allure.step("Train Name: " + trainName, () -> {
                formatTrainInfo(dateTimeInfo);

                for (WebElement seatInfoElement : seatInfoElements) {
                    String classInfo = seatInfoElement.findElement(By.xpath(".//p[@data-testid=\"class-info\"]")).getText();
                    String availabilityDetails = seatInfoElement.findElement(By.xpath(".//p[@data-testid=\"availability-text\"]")).getText();
                    String priceDetails = seatInfoElement.findElement(By.xpath(".//p[contains(@class, \"Cards_totalText\")]")).getText();
                    String priceOnly = priceDetails.replaceAll("\\D", "");
                    LoggerUtil.verify(Integer.parseInt(priceOnly) > 0, "For travel class = " + classInfo + " Price shall be non-zero | current ticket price = " + priceDetails + " | " + availabilityDetails);
                }
            });
        }
        return elementToClick;
    }
}
