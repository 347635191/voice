package 中文;

import java.io.File;
import java.util.regex.Pattern;

public class 检查阿木木 {
    private static final String FILE_PATH = "E:\\AAA\\sound\\国语\\阿木木(2024元旦)";

    public static void main(String[] args) throws Exception {
        File file = new File(FILE_PATH);
        for (File listFile : file.listFiles()) {
            if(!Pattern.matches("((2020|2021|2022|2023|2024|第一季|第二季|)[\\u4e00-\\u9fa50-9]+|未分类) [\\u4e00-\\u9fa5（）a-zA-Z0-9]+--[\\u4e00-\\u9fa5、]+.mp3", listFile.getName())){
                System.out.println(listFile.getName());
            }
        }
    }
}
