package 中文;

import java.io.File;
import java.util.regex.Pattern;

public class 检查逍遥 {
    private static final String FILE_PATH = "E:\\AAA\\sound\\国语\\逍遥(56部)";

    public static void main(String[] args) throws Exception {
        File file = new File(FILE_PATH);
        for (File listFile : file.listFiles()) {
            if(!Pattern.matches("[\\u4e00-\\u9fa5（）a-zA-Z0-9]+--[\\u4e00-\\u9fa5、]+.mp3", listFile.getName())){
                System.out.println(listFile.getName());
            }
        }
    }
}
