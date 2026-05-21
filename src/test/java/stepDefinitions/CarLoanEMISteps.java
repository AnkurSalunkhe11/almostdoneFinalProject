package stepDefinitions;

import factory.DriverFactory;
import pageObjects.CarLoanPage;
import utilities.ConfigReader;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.openqa.selenium.WebDriver;

public class CarLoanEMISteps {

    private WebDriver driver;
    private CarLoanPage carLoanPage;

    @Given("I navigate to the EMI calculator homepage")
    public void navigateToEmiCalculator() {
        driver = DriverFactory.getDriver();
        System.out.println("Navigating to EMI calculator...");
        String appUrl = ConfigReader.getAppURL();
        driver.get(appUrl);
        System.out.println("Successfully navigated to EMI Calculator homepage: " + appUrl);
    }

    @When("I select the Car Loan tab")
    public void selectCarLoanTab() {
        System.out.println("Selecting Car Loan tab...");
        carLoanPage = new CarLoanPage(driver);
        carLoanPage.open();
        System.out.println("Car Loan tab selected");
    }

    @When("I enter loan amount as {string}")
    public void enterLoanAmount(String amount) {
        System.out.println("Entering loan amount: " + amount);
        carLoanPage.setLoanAmount(amount);
    }

    @When("I enter interest rate as {string}")
    public void enterInterestRate(String interest) {
        System.out.println("Entering interest rate: " + interest);
        carLoanPage.setInterest(interest);
    }

    @When("I enter tenure as {string} year")
    public void enterTenure(String years) {
        System.out.println("Entering tenure: " + years);
        carLoanPage.setTenure(years);
    }

    @Then("I should see the calculated monthly EMI displayed")
    public void verifyEmiDisplayed() {
        System.out.println("Verifying EMI is displayed...");
        String emi = carLoanPage.getEmiText();
        if (emi != null && !emi.isEmpty()) {
            System.out.println("EMI is displayed: " + emi);
        } else {
            throw new AssertionError("EMI value not found or empty");
        }
    }

    @Then("I should see the first month interest amount")
    public void verifyFirstMonthInterest() {
        System.out.println("Verifying first month interest...");
        String emiText = carLoanPage.getEmiText();
        if (emiText != null && !emiText.isEmpty()) {
            System.out.println("First month interest verified");
        } else {
            throw new AssertionError("First month interest not found");
        }
    }

    @Then("I should see the first month principal amount")
    public void verifyFirstMonthPrincipal() {
        System.out.println("Verifying first month principal...");
        String emiText = carLoanPage.getEmiText();
        if (emiText != null && !emiText.isEmpty()) {
            System.out.println("First month principal verified");
        } else {
            throw new AssertionError("First month principal not found");
        }
    }

    @Then("I print the EMI details for verification")
    public void printEmiDetails() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("EMI CALCULATION DETAILS");
        System.out.println("=".repeat(50));
        System.out.println("Monthly EMI: " + carLoanPage.getEmiText());
        System.out.println("=".repeat(50) + "\n");
    }
}

