package utilities;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelUtil {

    public static void writeTableToExcel(List<List<String>> data, Path out) throws Exception {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Sheet1");
            int maxCols = 0;

            CellStyle numStyle = wb.createCellStyle();
            numStyle.setDataFormat(wb.createDataFormat().getFormat("#,##0.00"));

            for (int r = 0; r < data.size(); r++) {
                Row row = sheet.createRow(r);
                List<String> rowData = data.get(r);
                if (rowData == null) continue;
                maxCols = Math.max(maxCols, rowData.size());

                for (int c = 0; c < rowData.size(); c++) {
                    Cell cell = row.createCell(c);
                    String s = rowData.get(c) != null ? rowData.get(c).trim() : "";
                    
                    String cleaned = s.replaceAll("[^0-9.-]", "");
                    if (!cleaned.isEmpty() && !s.contains("%") && !s.contains("/") && !s.contains("-")) {
                        try {
                            cell.setCellValue(Double.parseDouble(cleaned));
                            cell.setCellStyle(numStyle);
                            continue;
                        } catch (Exception ignored) {}
                    }
                    cell.setCellValue(s);
                }
            }

            for (int i = 0; i < maxCols; i++) {
                try { sheet.autoSizeColumn(i); } catch (Exception ignored) {}
            }

            Files.createDirectories(out.getParent());
            try (FileOutputStream fos = new FileOutputStream(out.toFile())) {
                wb.write(fos);
            }
        }
    }

    public static Map<String, String> readExcelRow(String path, String sheetName, int rowNum) throws Exception {
        Map<String, String> data = new HashMap<>();
        try (FileInputStream fis = new FileInputStream(path);
             XSSFWorkbook wb = new XSSFWorkbook(fis)) {
            Sheet sheet = wb.getSheet(sheetName);
            Row row = sheet.getRow(rowNum);
            DataFormatter formatter = new DataFormatter();

            data.put("loanEmi", formatter.formatCellValue(row.getCell(0)).trim());
            data.put("loanInterest", formatter.formatCellValue(row.getCell(1)).trim());
            data.put("loanTerm", formatter.formatCellValue(row.getCell(2)).trim());
            data.put("loanFees", formatter.formatCellValue(row.getCell(3)).trim());
            data.put("loanAmount", formatter.formatCellValue(row.getCell(4)).trim());
        }
        return data;
    }
}
