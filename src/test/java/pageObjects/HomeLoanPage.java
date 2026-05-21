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
import java.util.ArrayList;
import java.util.List;

public class HomeLoanPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(xpath = "//a[text()='Home Loan']")
    private WebElement homeLoanTab;

    @FindBy(id = "loanamount")
    private WebElement loanAmount;

    @FindBy(id = "loaninterest")
    private WebElement loanInterest;

    @FindBy(id = "loanterm")
    private WebElement loanTerm;

    public HomeLoanPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
    }

    public void open() {
        wait.until(ExpectedConditions.elementToBeClickable(homeLoanTab)).click();
        wait.until(ExpectedConditions.visibilityOf(loanAmount));
    }

    private void clearAndType(WebElement el, String text) {
        wait.until(ExpectedConditions.visibilityOf(el)).click();
        el.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        el.sendKeys(text);
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].value = arguments[1];" +
            "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
            "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
            el, text);
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
        waitForTableReady();
    }

    public void waitForTableReady() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("table")));
        wait.until(d -> d.findElements(By.cssSelector("table tr")).size() > 1);
    }

    public void scrollToTable() {
        WebElement table = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("table")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", table);
        try { Thread.sleep(500); } catch (Exception ignored) {}
    }

    public List<List<String>> extractAllAmortizationData() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("table")));
        List<List<String>> allData = new ArrayList<>();
        List<WebElement> tables = driver.findElements(By.tagName("table"));
        if (tables.isEmpty()) return allData;
        WebElement mainTable = tables.get(0);

        // Collect headers
        List<String> headerRow = new ArrayList<>();
        for (WebElement h : mainTable.findElements(By.cssSelector("tr th"))) {
            String text = h.getText().trim();
            if (!text.isEmpty()) {
                headerRow.add(text);
            }
        }
        if (!headerRow.isEmpty()) allData.add(headerRow);

        // Show all monthly payment containers via JS
        ((JavascriptExecutor) driver).executeScript(
            "document.querySelectorAll('.monthlypaymentcontainer').forEach(el => el.style.display = 'block');"
        );

        // Extract main and nested table rows
        for (WebElement row : mainTable.findElements(By.tagName("tr"))) {
            List<WebElement> nestedTables = row.findElements(By.tagName("table"));
            if (!nestedTables.isEmpty()) {
                for (WebElement nested : nestedTables) {
                    for (WebElement nr : nested.findElements(By.tagName("tr"))) {
                        List<String> nestedRowData = new ArrayList<>();
                        for (WebElement c : nr.findElements(By.cssSelector("td, th"))) {
                            nestedRowData.add(c.getText().trim());
                        }
                        if (!nestedRowData.isEmpty() && !nestedRowData.equals(headerRow)) {
                            allData.add(nestedRowData);
                        }
                    }
                }
                continue;
            }

            List<String> rowData = new ArrayList<>();
            for (WebElement cell : row.findElements(By.tagName("td"))) {
                rowData.add(cell.getText().trim());
            }
            if (!rowData.isEmpty()) {
                allData.add(rowData);
            }
        }
        return allData;
    }
}
