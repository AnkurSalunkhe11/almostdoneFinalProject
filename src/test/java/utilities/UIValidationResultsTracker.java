package utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class UIValidationResultsTracker {
    public record ValidationRecord(String scenarioName, String fieldName, String expectedValue, String actualValue, String status) {}
    private static final List<ValidationRecord> records = new ArrayList<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(UIValidationResultsTracker::saveToExcel));
    }

    public static synchronized void logValidation(String sc, String field, String exp, String act, boolean passed) {
        String simplifiedSc = sc;
        if (sc != null) {
            String lowerSc = sc.toLowerCase();
            if (lowerSc.contains("monthly emi") || lowerSc.contains("emi calculator")) {
                simplifiedSc = "EMI Calculator";
            } else if (lowerSc.contains("total loan amount") || lowerSc.contains("loan amount calculator")) {
                simplifiedSc = "Loan Amount Calculator";
            } else if (lowerSc.contains("tenure") || lowerSc.contains("loan tenure calculator")) {
                simplifiedSc = "Loan Tenure Calculator";
            }
        }

        String simplifiedField = field;
        if (field != null) {
            String lowerField = field.toLowerCase();
            if (lowerField.contains("total loan amount")) {
                simplifiedField = "Total Loan Amount";
            } else if (lowerField.contains("loan amount")) {
                simplifiedField = "Loan Amount";
            } else if (lowerField.contains("interest rate") || lowerField.contains("interest")) {
                simplifiedField = "Interest Rate";
            } else if (lowerField.contains("loan term") || lowerField.contains("term")) {
                simplifiedField = "Loan Term";
            } else if (lowerField.contains("term toggle") || lowerField.contains("toggle")) {
                simplifiedField = "Term Toggle";
            } else if (lowerField.contains("monthly emi") || lowerField.contains("loan emi") || lowerField.contains("emi")) {
                simplifiedField = "Monthly EMI";
            } else if (lowerField.contains("loan tenure") || lowerField.contains("tenure")) {
                simplifiedField = "Loan Tenure";
            }
        }

        String status = passed ? "PASS" : "FAIL";
        records.add(new ValidationRecord(simplifiedSc, simplifiedField, exp, act, status));
        System.out.println("[TRACKER] " + simplifiedField + " | Expected: " + exp + " | Actual: " + act + " | Status: " + status);
    }

    public static synchronized void saveToExcel() {
        File file = new File("testdatafolder/UI_Validation_Results.xlsx");
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("UI Validation Results");

            CellStyle headerStyle = wb.createCellStyle();
            Font hFont = wb.createFont(); hFont.setBold(true); headerStyle.setFont(hFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle passStyle = wb.createCellStyle();
            Font pFont = wb.createFont(); pFont.setColor(IndexedColors.DARK_GREEN.getIndex()); pFont.setBold(true); passStyle.setFont(pFont);
            passStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            passStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle failStyle = wb.createCellStyle();
            Font fFont = wb.createFont(); fFont.setColor(IndexedColors.DARK_RED.getIndex()); fFont.setBold(true); failStyle.setFont(fFont);
            failStyle.setFillForegroundColor(IndexedColors.ROSE.getIndex());
            failStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            String[] headers = {"Scenario Name", "Validation Field", "Expected Value", "Actual Value", "Status"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell c = headerRow.createCell(i); c.setCellValue(headers[i]); c.setCellStyle(headerStyle);
            }

            int r = 1;
            for (ValidationRecord rec : records) {
                Row row = sheet.createRow(r++);
                row.createCell(0).setCellValue(rec.scenarioName());
                row.createCell(1).setCellValue(rec.fieldName());
                row.createCell(2).setCellValue(rec.expectedValue());
                row.createCell(3).setCellValue(rec.actualValue());
                Cell sc = row.createCell(4); sc.setCellValue(rec.status());
                sc.setCellStyle("PASS".equals(rec.status()) ? passStyle : failStyle);
            }

            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

            file.getParentFile().mkdirs();
            try (FileOutputStream fos = new FileOutputStream(file)) { wb.write(fos); }
            System.out.println("Results written to " + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
