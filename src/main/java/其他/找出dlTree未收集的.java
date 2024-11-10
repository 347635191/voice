package 其他;

import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSONPath;
import component.HttpClientUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 排除RJ434372、RJ01216049、RJ01119664、RJ01231234
 */
public class 找出dlTree未收集的 {
    private static StringBuilder outMsg = new StringBuilder("原版\t中文版\tONE包含\n");
    private static List<String> allRjs = new ArrayList<>();
    private static String fenLeiPath = "E:\\AAA\\sound\\日语\\分类";
    private static String xiLiePath = "E:\\AAA\\sound\\日语\\系列";
    private static String out = "C:\\Users\\lufeii\\Desktop\\out";
    private static String[] filterWords = new String[]{"全年龄", "乙女", "女性向"};

    public static void main(String[] args) {
        File[] fenLeiListFile = new File(fenLeiPath).listFiles();
        collect(fenLeiListFile);
        File[] xiLieListFile = new File(xiLiePath).listFiles();
        collect(xiLieListFile);

        //DL tree.xlsx
        searchExcel();

        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(out + "\\\\" + "lossRj.txt"))));
            bufferedWriter.write(outMsg.toString());
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void collect(File[] listFile) {
        for (File outerFile : listFile) {
            if (outerFile.getName().endsWith("ini")) {
                continue;
            }
            File[] files = outerFile.listFiles();
            for (File file : files) {
                if (file.getName().endsWith("ini")) {
                    continue;
                }
                String rj = file.getName().split(" ")[0];
                allRjs.add(rj);
            }
        }
    }

    private static String getCellVal(Cell cell) {
        if (cell.getCellTypeEnum() == CellType.ERROR || cell.getCellTypeEnum() == CellType.FORMULA) {
            return "";
        } else if (cell.getCellTypeEnum() == CellType.NUMERIC) {
            return String.valueOf(cell.getNumericCellValue());
        } else {
            return cell.getStringCellValue();
        }
    }

    private static void searchExcel() {
        XSSFWorkbook xssfWorkbook = null;
        try {
            xssfWorkbook = new XSSFWorkbook(new File("D:\\BaiduNetdiskDownload\\DL tree.xlsx"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        XSSFSheet sheet = xssfWorkbook.getSheet("Sheet1");
        Iterator<Row> rowIterator = sheet.rowIterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            String cell6 = getCellVal(row.getCell(6));
            if (!StringUtils.contains(cell6, "官方汉化")) {
                continue;
            }
            String rj = getRj(cell6);
            String cell5 = getCellVal(row.getCell(5));//标题
            String cell3 = getCellVal(row.getCell(3));//TAG
            if (StringUtils.isBlank(rj) || allRjs.contains(rj)) {
                continue;
            }
            if (StringUtils.containsAny(cell3, filterWords) || StringUtils.containsAny(cell5, filterWords)) {
                continue;
            }
            if (outMsg.toString().contains(rj)) {
                continue;
            }
            String rjInfo = HttpClientUtils.getRjInfo(rj);
            boolean oneHas = false;
            if (StringUtils.isNotBlank(rjInfo)) {
                if (rjInfo.contains("error")) {
                    continue;
                }
                oneHas = true;
                JSONObject jsonObject = new JSONObject(rjInfo);
                String age = (String) JSONPath.eval(jsonObject, "$.age_category_string");
                //全年龄
                if (StringUtils.equals("general", age)) {
                    continue;
                }
                if (StringUtils.containsAny(rjInfo, "女性向", "乙女向")) {
                    continue;
                }
            }
            String cell0 = getCellVal(row.getCell(0));
            outMsg.append(rj).append('\t').append(cell0.trim()).append('\t').append(oneHas ? "是" : "否").append('\n');
        }
    }

    private static String getRj(String word) {
        String regex = "RJ\\d+";
        Matcher matcher = Pattern.compile(regex).matcher(word);
        while (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
}