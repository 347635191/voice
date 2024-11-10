package 日常检查;

import org.apache.commons.lang3.StringUtils;
import 实体类.统一文件接口;

import java.io.*;

public class 校验字幕是否包含搬运汉化组等字符 implements 统一文件接口 {
    public static void main(String[] args) throws Exception {
        new 校验字幕是否包含搬运汉化组等字符().process();
    }

    public void handle(File file) throws IOException {
        for (File file1 : file.listFiles()) {
            if (file1.getName().endsWith(".lrc")) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file1.getAbsolutePath())));
                String str;
                while ((str = bufferedReader.readLine()) != null) {
                    if ((StringUtils.containsAny(str, "汉化", "字幕", "转载", "北极", "翻译")) && !str.contains("字幕待汉化")) {
                        System.out.println(file1.getAbsolutePath());
                        System.out.println(str);
                    }
                }
                bufferedReader.close();
            }
        }
    }
}
