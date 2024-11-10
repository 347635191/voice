package 中文;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class 检查重复的音声 {
    private static Map<Long, String> map = new HashMap<>();

    public static void main(String[] args) {
        process(new File("E:\\AAA\\sound\\国语\\柒柒(原糖果屋)"));
    }

    private static void process(File root) {
        if (root.isDirectory()) {
            File[] files = root.listFiles();
            if (files != null) {
                for (File file : files) {
                    process(file);
                }
            }
        } else {
            if (root.getName().endsWith(".mp3")) {
                if (map.containsKey(root.length())) {
                    System.out.println(map.get(root.length()));
                    System.out.println(root.getName());
                } else {
                    map.put(root.length(), root.getName());
                }
            }
        }
    }
}
