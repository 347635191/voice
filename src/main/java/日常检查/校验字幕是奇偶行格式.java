package 日常检查;

import org.apache.commons.lang3.StringUtils;
import 实体类.统一文件接口;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

/**
 * 检查奇数行是否有字
 * 检查偶数行是否为空格
 * 检查第一行是否为[00:00.00]且不为标题或者字幕待汉化
 * 校验字幕格式
 */
public class 校验字幕是奇偶行格式 implements 统一文件接口 {
    public static void main(String[] args) throws Exception {
        new 校验字幕是奇偶行格式().process();
    }

    public void handle(File file) throws Exception {
        File[] files1 = file.listFiles();
        for (File file1 : files1) {
            if (file1.getName().endsWith(".lrc")) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file1.getAbsolutePath())));
                String str;
                int index = 0;
                while ((str = bufferedReader.readLine()) != null) {
                    if (!Pattern.matches("\\[\\d{2,3}:\\d{2}.\\d{2}].+", str)) {
                        System.out.println("字幕格式不正确" + str);
                        System.out.println(file1.getAbsolutePath());
                    }
                    int subIndex = 10;
                    if (StringUtils.substring(str, 10, 11).equals("]")) {
                        subIndex = 11;
                    }
                    String subString = str.substring(subIndex);
//                    if (str.contains("[00:00.00]")) {
//                        String str2 = file1.getName().substring(file1.getName().indexOf('.') + 1, file1.getName().length() - 4).replace("【H】", "");
//                        if (!subString.equals(str2) && !subString.equals("字幕待汉化")) {
//                            System.out.println("字幕格式不正确" + str);
//                            System.out.println(file1.getAbsolutePath());
//                        }
//                    }
                    if(StringUtils.containsAny(str,"fad", "move")){
                        System.out.println("字幕格式不正确" + str);
                        System.out.println(file1.getAbsolutePath());
                    }
                    if (index % 2 == 0) {
                        if (StringUtils.isBlank(subString)) {
                            System.out.println("奇数");
                            System.out.println(str);
                            System.out.println(file1.getAbsolutePath());
                        }
                    } else {
                        if (!StringUtils.equals(" ", subString)) {
                            System.out.println("偶数");
                            System.out.println(str);
                            System.out.println(file1.getAbsolutePath());
                            break;
                        }
                    }
                    index++;
                }
                bufferedReader.close();
            }
        }
    }
}
