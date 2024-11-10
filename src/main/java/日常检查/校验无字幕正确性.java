package 日常检查;

import org.apache.commons.lang3.StringUtils;
import 实体类.统一文件接口;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class 校验无字幕正确性 implements 统一文件接口 {
    public static void main(String[] args) throws Exception {
        new 校验无字幕正确性().process();
    }

    public void handle(File file) throws Exception {
        if (file.getName().contains("RJ01154125")) {
            return;
        }
        File[] files1 = file.listFiles();
        for (File file1 : files1) {
            if (file1.getName().endsWith(".lrc")) {
                if(file1.getName().contains("骰子")){
                    continue;
                }
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file1.getAbsolutePath())));
                String firstLine = bufferedReader.readLine();
                String secondLine = bufferedReader.readLine();
                if (StringUtils.isBlank(secondLine)) {
                    System.out.println(file1.getAbsolutePath() + "少于两行");
                    bufferedReader.close();
                    break;
                }
                String thirdLine = bufferedReader.readLine();
                if (StringUtils.isBlank(thirdLine)) {
                    String str1 = firstLine.substring(10);
                    String str2 = file1.getName().substring(file1.getName().indexOf('.') + 1, file1.getName().length() - 4).replace("【H】", "");
                    if (!str1.equals(str2) && !str1.equals("字幕待汉化")) {
                        System.out.println(str1);
                        System.out.println(str2);
                        System.out.println(file1.getAbsolutePath());
                    }
                }
                bufferedReader.close();
            }
        }
    }
}