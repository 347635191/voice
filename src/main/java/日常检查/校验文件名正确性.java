package 日常检查;

import org.apache.commons.lang3.StringUtils;
import 实体类.统一文件接口;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 忘记加Hma
 */
public class 校验文件名正确性 implements 统一文件接口 {
    private static List<String> list = Arrays.asList("对面座位", "后背位", "骑乘位", "正常位", "背面座位", "对面立位", "背面立位", "对面侧位", "背面侧位", "屈曲位");

    public static void main(String[] args) throws Exception {
        new 校验文件名正确性().process();
    }

    public void handle(File file) {
        String name = file.getName();
        if (name.startsWith(" ") || name.endsWith("  ") || name.contains("  ") || name.contains("鸡巴")) {
            System.out.println(file.getAbsolutePath());
        }
        File[] files = file.listFiles();
        if (files == null) {
            return;
        }
        for (File file1 : files) {
            if (validName(file1.getName())) {
                System.out.println(file1.getName());
                System.out.println(file.getAbsolutePath());
            }
        }
    }

    private static boolean validName(String name) {
        if (name.endsWith("mp3")) {
            if ((name.contains("内射") && !StringUtils.containsAny(name, "口内射精", "腔内射精"))) {
                return true;
            }
            if (!Pattern.matches("\\d{1,3}\\.(【H】)?.*[^ ]\\.mp3", name) || StringUtils.containsAny(name, "  ", "鸡巴", "　")) {
                return true;
            }
            if (StringUtils.containsAny(name, "..", "♪", "。", "wav", "mp3.mp3", "_", "…", "…", "『", "』")) {
                return true;
            }
            for (String keyword : list) {
                if (name.contains(keyword) && !name.contains("【H】") && !name.contains("导入")) {
                    return true;
                }
            }
            if (!Pattern.matches("\\d{1,3}.(【H】)?[\\u4e00-\\u9fa5\\d A-Z&→%？a-z○αβ=\\-〇⇒~]+.mp3", name)) {
                return true;
            }
        }
        return false;
    }
}
