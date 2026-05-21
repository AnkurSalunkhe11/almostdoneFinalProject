package pageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoanCalculatorPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final JavascriptExecutor js;
    private static final int TIMEOUT_SECONDS = 15;

    @FindBy(id = "emi-calc")
    private WebElement emiCalculatorTab;

    @FindBy(id = "loan-amount-calc")
    private WebElement loanAmountCalculatorTab;

    @FindBy(id = "loan-tenure-calc")
    private WebElement loanTenureCalculatorTab;

    public LoanCalculatorPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_SECONDS));
        this.js = (JavascriptExecutor) driver;
        PageFactory.initElements(driver, this);
    }

    public void selectEmiCalculatorTab() {
        wait.until(ExpectedConditions.elementToBeClickable(emiCalculatorTab)).click();
        pause(2);
    }

    public void selectLoanAmountCalculatorTab() {
        wait.until(ExpectedConditions.elementToBeClickable(loanAmountCalculatorTab)).click();
        pause(2);
    }

    public void selectLoanTenureCalculatorTab() {
        wait.until(ExpectedConditions.elementToBeClickable(loanTenureCalculatorTab)).click();
        pause(2);
    }

    public void clearAndSetInputValue(By locator, String fieldName, String value) {
        System.out.println("[INPUT] Setting " + fieldName + " to: " + value);
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        element.click();
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        js.executeScript("arguments[0].value = '';", element);
        element.sendKeys(value);
        js.executeScript(
            "var el = arguments[0];" +
            "el.dispatchEvent(new Event('input', { bubbles: true }));" +
            "el.dispatchEvent(new Event('change', { bubbles: true }));" +
            "el.dispatchEvent(new Event('blur', { bubbles: true }));" +
            "if (window.jQuery) { " +
            "   window.jQuery(el).trigger('input').trigger('change').trigger('keyup').trigger('blur'); " +
            "}", 
            element
        );
        pause(1);
    }

    public String getInputValue(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator)).getAttribute("value");
    }

    public String getCalculatedOutputText(String cssOrXpath) {
        try {
            WebElement el = cssOrXpath.startsWith("//") ? 
                driver.findElement(By.xpath(cssOrXpath)) : driver.findElement(By.cssSelector(cssOrXpath));
            return el.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public String getActiveTermToggle() {
        return (Boolean) js.executeScript("return document.getElementById('loanyears').checked") ? "Years" : "Months";
    }

    private void pause(int sec) {
        try {
            Thread.sleep(sec * 1000L);
        } catch (InterruptedException ignored) {}
    }
}
