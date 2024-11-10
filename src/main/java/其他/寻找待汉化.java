package 其他;

import 实体类.统一文件接口;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class 寻找待汉化 implements 统一文件接口 {
    private static List<String> daihanhuaList = new ArrayList<>();
    private static String rootPath = "E:\\AAA\\new\\风花雪月合集v";

    public static void main(String[] args) throws Exception {
        new 寻找待汉化().process();
        search(new File(rootPath));
    }

    public void handle(File file) throws Exception {
        for (File listFile : file.listFiles()) {
            if (listFile.getName().endsWith(".lrc")) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Files.newInputStream(listFile.toPath())));
                String str = bufferedReader.readLine();
                bufferedReader.close();
                if (str.contains("字幕待汉化")) {
                    daihanhuaList.add(file.getName().split(" ")[0].substring(2));
                    break;
                }
            }
        }
    }

    private static void search(File root) {
        String name = root.getName().toLowerCase(Locale.ROOT);
        for (String s : daihanhuaList) {
            if (name.contains(s.toLowerCase(Locale.ROOT))) {
                System.out.println(root.getAbsolutePath());
            }
        }
        if (root.isDirectory()) {
            File[] files = root.listFiles();
            if (files != null) {
                for (File file : files) {
                    search(file);
                }
            }
        }
    }
}