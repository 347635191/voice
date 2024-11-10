package 日常检查;

import org.apache.commons.lang3.StringUtils;
import 实体类.统一文件接口;

import java.io.File;
import java.util.regex.Pattern;

public class 检查图片名称正确性 implements 统一文件接口 {
    public static void main(String[] args) throws Exception {
        new 检查图片名称正确性().process();
    }

    public void handle(File file) {
        for (File listFile : file.listFiles()) {
            String name = listFile.getName();
            if (StringUtils.equalsAny(name, "folder.jpg", "cover.jpg", "desktop.ini","cover.png")
                    || StringUtils.endsWith(name, ".mp3") || StringUtils.endsWith(name, ".lrc")) {
                continue;
            }
            if (!Pattern.matches("main\\d{0,3}\\.(jpg|png)", listFile.getName())) {
                System.out.println(listFile.getName());
                System.out.println(file.getAbsolutePath());
            }
        }
    }
}
