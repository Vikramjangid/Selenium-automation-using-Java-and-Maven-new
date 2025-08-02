
# Selenium-automation-using-Java-and-Maven-new

This repository contains a robust Selenium automation framework built using Java and Maven. The framework is designed for end-to-end UI automation of the MakeMyTrip website, focusing on booking train tickets. It demonstrates best practices in test automation, including the use of the Page Object Model (POM), reusable utilities, configuration management, and detailed reporting with Allure.

## Repository Overview

- **Purpose:** Automate the process of searching and booking train tickets on MakeMyTrip, including filling traveller information and verifying booking details.
- **Design:** Modular and extensible, allowing easy addition of new test cases and support for multiple browsers (Chrome, Firefox).
- **Reporting:** Generates rich test reports using Allure, including screenshots and step-by-step logs.
- **Configuration:** All environment-specific values (URL, browser, etc.) are managed via a config file for easy switching and CI/CD integration.

## Technologies Used
- Java (JDK 8 or above)
- Selenium WebDriver
- Maven
- TestNG
- Allure Reporting
- WebDriverManager

## Project Structure

- `src/main/java/com/framework/base/` - Core classes like DriverFactory and BasePage
- `src/main/java/com/framework/pages/` - Page Object Model classes for each web page
- `src/main/java/com/framework/utils/` - Utility classes (logging, config, date, etc.)
- `src/test/java/com/framework/tests/` - Test classes
- `resources/config.properties` - Configuration file for environment variables
- `allure-results/` - Allure test results (generated after test run)

## Setup Instructions
1. **Clone the repository:**
   ```
   git clone <repository-url>
   cd Selenium-automation-using-Java-and-Maven
   ```
2. **Install Java and Maven:**
   - Ensure Java (JDK 8 or above) and Maven are installed and available in your PATH.
   - Verify installation:
     ```
     java -version
     mvn -version
     ```
3. **Install dependencies:**
   - Download all project dependencies:
     ```
     mvn clean install
     ```
4. **Allure installation:**
   - Download and install Allure commandline from [Allure documentation](https://docs.qameta.io/allure/).
   - Ensure `allure` is available in your system PATH.
   - Verify installation:
     ```
     allure --version
     ```

## Configuration

- Edit `resources/config.properties` to set the base URL, browser, and other environment-specific values:
  ```
  baseUrl=https://www.makemytrip.com
  browser=chrome
  ```
- You can override the browser at runtime using a system property:
  ```
  mvn clean test -Dbrowser=firefox
  ```

## How to Run Tests
1. **Run the tests:**
   ```
   mvn clean test
   ```
2. **Generate and view Allure report:**
   ```
   allure serve allure-results
   ```
   This will open the Allure report in your default browser.

## Key Features
- Thread-safe WebDriver management for parallel test execution
- Page Object Model for maintainable and scalable tests
- Centralized configuration and logging
- Allure reporting with screenshots and step logs

## Extending the Framework
- Add new test cases in `src/test/java/com/framework/tests/`
- Add new page objects in `src/main/java/com/framework/pages/`
- Add new config values in `resources/config.properties`

## Troubleshooting
- Ensure all dependencies are installed and up to date (`mvn clean install`)
- If Allure report does not open, check that Allure is installed and in your PATH
- For browser issues, check the browser driver version and update WebDriverManager if needed
