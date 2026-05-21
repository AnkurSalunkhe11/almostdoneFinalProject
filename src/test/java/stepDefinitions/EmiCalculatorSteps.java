package stepDefinitions;

import factory.DriverFactory;
import pageObjects.LoanCalculatorPage;
import utilities.ExcelUtil;
import utilities.ScenarioContext;
import utilities.UIValidationResultsTracker;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.junit.Assert;
import java.io.File;
import java.util.Map;

public class EmiCalculatorSteps {
    private WebDriver driver;
    private LoanCalculatorPage loanCalculatorPage;
    private final ScenarioContext scenarioContext;

    public EmiCalculatorSteps(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @Given("I navigate to the Loan Calculator page")
    public void navigateToLoanCalculator() {
        driver = DriverFactory.getDriver();
        loanCalculatorPage = new LoanCalculatorPage(driver);
        driver.get("https://emicalculator.net/loan-calculator/");
        pause(3);
    }

    @Given("I read test data from sheet {string} and row {int} of {string}")
    public void readTestData(String sheet, int row, String file) throws Exception {
        File f = new File("testdatafolder/" + file);
        if (!f.exists()) f = new File(file);
        if (!f.exists()) throw new java.io.FileNotFoundException("Excel file not found: " + f.getAbsolutePath());
        scenarioContext.setContext("excelData", ExcelUtil.readExcelRow(f.getAbsolutePath(), sheet, row));
    }

    @When("I select the EMI Calculator tab")
    public void selectEmiCalculatorTab() { loanCalculatorPage.selectEmiCalculatorTab(); }

    @SuppressWarnings("unchecked")
    private Map<String, String> getExcelData() {
        return (Map<String, String>) scenarioContext.getContext("excelData");
    }

    @When("I enter the loan details from Excel on the EMI Calculator tab")
    public void enterLoanDetailsEmiCalculator() {
        Map<String, String> d = getExcelData();
        loanCalculatorPage.clearAndSetInputValue(By.id("loanamount"), "Loan Amount", d.get("loanAmount"));
        loanCalculatorPage.clearAndSetInputValue(By.id("loaninterest"), "Interest Rate", d.get("loanInterest"));
        loanCalculatorPage.clearAndSetInputValue(By.id("loanterm"), "Loan Tenure", d.get("loanTerm"));
        loanCalculatorPage.clearAndSetInputValue(By.id("loanfees"), "Fees & Charges", d.get("loanFees"));
        pause(3);
    }

    @Then("I should verify input fields on the EMI Calculator tab match the Excel test data")
    public void verifyInputFieldsEmiCalculator() {
        Map<String, String> d = getExcelData();
        assertMatch(loanCalculatorPage.getInputValue(By.id("loanamount")), d.get("loanAmount"), "Loan Amount", 5.0);
        assertMatch(loanCalculatorPage.getInputValue(By.id("loaninterest")), d.get("loanInterest"), "Interest Rate", 5.0);
        assertMatch(loanCalculatorPage.getInputValue(By.id("loanterm")), d.get("loanTerm"), "Loan Term", 5.0);
        assertToggleMatch(loanCalculatorPage.getActiveTermToggle(), "Years", "Term Toggle");
    }

    @Then("I should verify the computed Monthly EMI matches the Excel test data")
    public void verifyComputedMonthlyEmi() {
        Map<String, String> d = getExcelData();
        assertMatch(loanCalculatorPage.getCalculatedOutputText("#loansummary-emi span"), d.get("loanEmi"), "Monthly EMI", 10.0);
    }

    private void assertToggleMatch(String actual, String expected, String label) {
        String scName = scenarioContext.getScenario() != null ? scenarioContext.getScenario().getName() : "Validation";
        boolean passed = actual.equals(expected);
        UIValidationResultsTracker.logValidation(scName, label, expected, actual, passed);
        if (!passed) {
            Assert.fail(label + " mismatch. Expected: " + expected + ", Actual: " + actual);
        }
    }

    private void assertMatch(String actual, String expected, String label, double delta) {
        String scName = scenarioContext.getScenario() != null ? scenarioContext.getScenario().getName() : "Validation";
        if (actual == null || actual.trim().isEmpty()) {
            UIValidationResultsTracker.logValidation(scName, label, expected, "Empty", false);
            Assert.fail(label + " is empty");
        }
        double act = Double.parseDouble(actual.replaceAll("[^0-9.]", ""));
        double exp = Double.parseDouble(expected.replaceAll("[^0-9.]", ""));
        try {
            Assert.assertEquals(exp, act, delta);
            UIValidationResultsTracker.logValidation(scName, label, expected, actual, true);
        } catch (AssertionError e) {
            UIValidationResultsTracker.logValidation(scName, label, expected, actual, false);
            throw e;
        }
    }

    private void pause(int sec) {
        try { Thread.sleep(sec * 1000L); } catch (Exception ignored) {}
    }
}
