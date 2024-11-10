package 单独处理;

import com.ibm.icu.text.Transliterator;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class 桌面字幕繁体转简体 {
    private static String path = "C:\\Users\\lufeii\\Desktop";
    private static StringBuilder outStr = new StringBuilder();

    public static void main(String[] args) throws Exception {
//        File file = new File(path);
//        File[] files = file.listFiles();
//        for (int i = 0; i < files.length; i++) {
//            String absolutePath = files[i].getAbsolutePath();
//            if (absolutePath.endsWith(".lrc") || absolutePath.endsWith(".LRC") || absolutePath.endsWith(".txt")) {
//                replaceChar(files[i]);
//            }
//        }
        outer();
    }

    public static String outer() {
        outStr = new StringBuilder();
        File file = new File(path);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            String absolutePath = files[i].getAbsolutePath();
            if (absolutePath.endsWith(".lrc") || absolutePath.endsWith(".LRC") || absolutePath.endsWith(".txt")) {
                try {
                    replaceChar(files[i]);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        outStr.append("繁体转简体");
        return outStr.toString();
    }

    private static String simplify(String str) {
        str = str.replaceAll("妳", "你");
        str = str.replaceAll("噁", "恶");
        str = str.replaceAll("拚", "拼");
        str = str.replaceAll("沒", "没");
        str = str.replaceAll("吶", "呐");
        str = str.replaceAll("睪", "睾");
        str = str.replaceAll("麼", "么");

        str = str.replaceAll("「", "12345678");
        str = str.replaceAll("」", "87654321");
        Transliterator converter = Transliterator.getInstance("Traditional-Simplified");
        String afterStr = converter.transliterate(str);
        afterStr = afterStr.replaceAll("12345678", "「");
        return afterStr.replaceAll("87654321", "」");
    }

    private static void replaceChar(File file) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file.getAbsolutePath())));
        String str;
        StringBuilder result = new StringBuilder();
        while ((str = bufferedReader.readLine()) != null) {
            String simplify = simplify(str);
            if (!str.equals(simplify)) {
                System.out.println(str);
                System.out.println(simplify + '\n');
                outStr.append(str + "\n").append(simplify + '\n');
            }
            result.append(simplify + '\n');
        }
        bufferedReader.close();
        file.delete();
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get("C:\\Users\\lufeii\\Desktop\\" + simplify(file.getName().replace(".txt", ".lrc"))))));
        bufferedWriter.write(result.substring(0, result.length() - 1));
        bufferedWriter.flush();
        bufferedWriter.close();
    }
}