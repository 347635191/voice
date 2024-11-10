package 中文;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.regex.Pattern;

public class 检查柒柒 {
    private static final String FILE_PATH = "E:\\AAA\\sound\\国语\\柒柒(原糖果屋)";

    public static void main(String[] args) throws Exception {
        File file = new File(FILE_PATH);
        for (File listFile : file.listFiles()) {
            if(StringUtils.containsAny(listFile.getName().toLowerCase(), "小羊","宋恩","candy")){
                System.out.println(listFile.getName());
                continue;
            }
            if(!Pattern.matches("(糖果屋|第一季\\d+|第二季\\d+) [\\u4e00-\\u9fa5（）a-zA-Z0-9の【】 ]+--[\\u4e00-\\u9fa5、]+.mp3", listFile.getName())){
                System.out.println(listFile.getName());
            }
        }
    }
}
