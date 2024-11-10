package 日常检查;

import 实体类.统一文件接口;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class 校验一行多时间的字幕 implements 统一文件接口 {
    public static void main(String[] args) throws Exception {
        new 校验一行多时间的字幕().process();
    }

    public void handle(File file) throws Exception {
        File[] files1 = file.listFiles();
        for (File file1 : files1) {
            if (file1.getName().endsWith(".lrc")) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file1.getAbsolutePath())));
                String str;
                while ((str = bufferedReader.readLine()) != null) {
                    String str1 = str.substring(10);
                    if (str1.contains("[") && str1.contains("]")) {
                        System.out.println(str);
                        System.out.println(file1.getAbsolutePath());
                    }
                }
                bufferedReader.close();
            }
        }
    }
}
