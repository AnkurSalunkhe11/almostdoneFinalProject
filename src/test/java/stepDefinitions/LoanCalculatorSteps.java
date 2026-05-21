package stepDefinitions;

import factory.DriverFactory;
import pageObjects.LoanCalculatorPage;
import utilities.ScenarioContext;
import utilities.UIValidationResultsTracker;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.junit.Assert;
import java.util.Map;

public class LoanCalculatorSteps {
    private WebDriver driver;
    private LoanCalculatorPage loanCalculatorPage;
    private final ScenarioContext scenarioContext;

    public LoanCalculatorSteps(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    private void initPage() {
        if (driver == null) {
            driver = DriverFactory.getDriver();
            loanCalculatorPage = new LoanCalculatorPage(driver);
        }
    }

    @When("I select the Loan Amount Calculator tab")
    public void selectLoanAmountCalculatorTab() {
        initPage();
        loanCalculatorPage.selectLoanAmountCalculatorTab();
    }

    @When("I select the Loan Tenure Calculator tab")
    public void selectLoanTenureCalculatorTab() {
        initPage();
        loanCalculatorPage.selectLoanTenureCalculatorTab();
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> getExcelData() {
        return (Map<String, String>) scenarioContext.getContext("excelData");
    }

    @When("I enter the loan details from Excel on the Loan Amount Calculator tab")
    public void enterLoanDetailsLoanAmountCalculator() {
        initPage();
        Map<String, String> d = getExcelData();
        loanCalculatorPage.clearAndSetInputValue(By.id("loanemi"), "Loan EMI", d.get("loanEmi"));
        loanCalculatorPage.clearAndSetInputValue(By.id("loaninterest"), "Interest Rate", d.get("loanInterest"));
        loanCalculatorPage.clearAndSetInputValue(By.id("loanterm"), "Loan Tenure", d.get("loanTerm"));
        loanCalculatorPage.clearAndSetInputValue(By.id("loanfees"), "Fees & Charges", d.get("loanFees"));
        pause(3);
    }

    @Then("I should verify input fields on the Loan Amount Calculator tab match the Excel test data")
    public void verifyInputFieldsLoanAmountCalculator() {
        initPage();
        Map<String, String> d = getExcelData();
        assertMatch(loanCalculatorPage.getInputValue(By.id("loanemi")), d.get("loanEmi"), "Loan EMI", 5.0);
        assertMatch(loanCalculatorPage.getInputValue(By.id("loaninterest")), d.get("loanInterest"), "Interest Rate", 0.05);
        assertMatch(loanCalculatorPage.getInputValue(By.id("loanterm")), d.get("loanTerm"), "Loan Term", 0.05);
        assertToggleMatch(loanCalculatorPage.getActiveTermToggle(), "Years", "Term Toggle");
    }

    @Then("I should verify the computed Total Loan Amount matches the Excel test data")
    public void verifyComputedTotalLoanAmount() {
        initPage();
        Map<String, String> d = getExcelData();
        assertMatch(loanCalculatorPage.getCalculatedOutputText("#loansummary-loanamount span"), d.get("loanAmount"), "Total Loan Amount", 5000.0);
    }

    @When("I enter the loan details from Excel on the Loan Tenure Calculator tab")
    public void enterLoanDetailsLoanTenureCalculator() {
        initPage();
        Map<String, String> d = getExcelData();
        loanCalculatorPage.clearAndSetInputValue(By.id("loanamount"), "Loan Amount", d.get("loanAmount"));
        loanCalculatorPage.clearAndSetInputValue(By.id("loanemi"), "Loan EMI", d.get("loanEmi"));
        loanCalculatorPage.clearAndSetInputValue(By.id("loaninterest"), "Interest Rate", d.get("loanInterest"));
        loanCalculatorPage.clearAndSetInputValue(By.id("loanfees"), "Fees & Charges", d.get("loanFees"));
        pause(3);
    }

    @Then("I should verify input fields on the Loan Tenure Calculator tab match the Excel test data")
    public void verifyInputFieldsLoanTenureCalculator() {
        initPage();
        Map<String, String> d = getExcelData();
        assertMatch(loanCalculatorPage.getInputValue(By.id("loanamount")), d.get("loanAmount"), "Loan Amount", 10.0);
        assertMatch(loanCalculatorPage.getInputValue(By.id("loanemi")), d.get("loanEmi"), "Loan EMI", 10.0);
        assertMatch(loanCalculatorPage.getInputValue(By.id("loaninterest")), d.get("loanInterest"), "Interest Rate", 0.05);
    }

    @Then("I should verify the computed Loan Tenure matches the Excel test data")
    public void verifyComputedLoanTenure() {
        initPage();
        Map<String, String> d = getExcelData();
        String val = loanCalculatorPage.getCalculatedOutputText("#loansummary-tenure span");
        Assert.assertFalse("Tenure output is empty", val.isEmpty());
        double actualYears = Double.parseDouble(val.replaceAll("[^0-9.]", "")) / 12.0;
        double expectedYears = Double.parseDouble(d.get("loanTerm").replaceAll("[^0-9.]", ""));
        
        String scName = scenarioContext.getScenario() != null ? scenarioContext.getScenario().getName() : "Tenure Validation";
        try {
            Assert.assertEquals(expectedYears, actualYears, 0.05);
            UIValidationResultsTracker.logValidation(scName, "Loan Tenure", expectedYears + " Years", actualYears + " Years", true);
        } catch (AssertionError e) {
            UIValidationResultsTracker.logValidation(scName, "Loan Tenure", expectedYears + " Years", actualYears + " Years", false);
            throw e;
        }
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
