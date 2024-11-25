package com.yf.rj.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class XmlUtil {
    public static String getCellVal(Cell cell) {
        if (cell.getCellTypeEnum() == CellType.ERROR || cell.getCellTypeEnum() == CellType.FORMULA) {
            return "";
        } else if (cell.getCellTypeEnum() == CellType.NUMERIC) {
            return String.valueOf(cell.getNumericCellValue());
        } else {
            return cell.getStringCellValue();
        }
    }
}