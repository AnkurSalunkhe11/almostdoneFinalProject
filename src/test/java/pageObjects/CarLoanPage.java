package pageObjects;

import org.openqa.selenium.Keys;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import utilities.ScreenshotUtil;

public class CarLoanPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private static final int TIMEOUT_SECONDS = 15;

    @FindBy(xpath = "//a[text()='Car Loan']")
    private WebElement carLoanTab;

    @FindBy(id = "loanamount")
    private WebElement loanAmount;

    @FindBy(id = "loaninterest")
    private WebElement loanInterest;

    @FindBy(id = "loanterm")
    private WebElement loanTerm;

    @FindBy(id = "emiamount")
    private WebElement emiAmount;

    public CarLoanPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_SECONDS));
        PageFactory.initElements(driver, this);
    }

    public void open() {
        wait.until(ExpectedConditions.elementToBeClickable(carLoanTab));
        carLoanTab.click();
    }

    private void clearAndType(WebElement el, String text) {
        el.click();
        el.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        el.sendKeys(Keys.DELETE);
        el.sendKeys(text);
    }

    public void setLoanAmount(String amount) {
        clearAndType(loanAmount, amount);
    }

    public void setInterest(String interest) {
        clearAndType(loanInterest, interest);
    }

    public void setTenure(String years) {
        clearAndType(loanTerm, years);
        loanTerm.sendKeys(Keys.ENTER);
    }

    // Simplified combined flow: enter inputs and take a screenshot
    public void applyInputsAndCapture(String amount, String interest, String years) {
        if (amount != null && !amount.isBlank()) {
            clearAndType(loanAmount, amount);
        }
        if (interest != null && !interest.isBlank()) {
            clearAndType(loanInterest, interest);
        }
        if (years != null && !years.isBlank()) {
            clearAndType(loanTerm, years);
            loanTerm.sendKeys(Keys.ENTER);
        }
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,600);");
        ScreenshotUtil.takeScreenshot("car_loan_after_input");
    }

    public String getEmiText() {
        return emiAmount.getText();
    }
}

