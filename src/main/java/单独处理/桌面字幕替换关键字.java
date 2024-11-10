package 单独处理;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class 桌面字幕替换关键字 {
    private static String path = "C:\\Users\\lufeii\\Desktop";
    private static String out = "C:\\Users\\lufeii\\Desktop";
    private static String KEY_WORD = "Tooka";
    private static String REPLACEMENT = "十花";
    private static StringBuilder outStr = new StringBuilder();

    public static void main(String[] args) throws Exception {
        File file = new File(path);
        File[] files = file.listFiles();
        for (File value : files) {
            String name = value.getName();
            if (name.endsWith(".lrc")) {
                replaceChar(value);
            }
        }
    }

    public static String outer(String[] split) {
        outStr = new StringBuilder();
        KEY_WORD = split[0];
        REPLACEMENT = split[1];
        File file = new File(path);
        File[] files = file.listFiles();
        for (File value : files) {
            String name = value.getName();
            if (name.endsWith(".lrc")) {
                try {
                    replaceChar(value);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        outStr.append("替换关键字成功");
        return outStr.toString();
    }

    private static void replaceChar(File file) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(file.getAbsolutePath()))));
        String fileName = file.getName();
        if (fileName.toLowerCase().contains(KEY_WORD.toLowerCase())) {
            System.out.println(fileName);
            outStr.append(fileName).append("\n");
            fileName = StringUtils.replaceIgnoreCase(file.getName(), KEY_WORD, REPLACEMENT);
        }
        String str;
        StringBuilder result = new StringBuilder();
        while ((str = bufferedReader.readLine()) != null) {
            if (str.toLowerCase().contains(KEY_WORD.toLowerCase())) {
                System.out.println(str);
                outStr.append(str).append("\n");
                str = StringUtils.replaceIgnoreCase(str, KEY_WORD, REPLACEMENT);
            }
            result.append(str + '\n');
        }
        bufferedReader.close();
        file.delete();
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(out + "\\\\" + fileName))));
        bufferedWriter.write(result.substring(0, result.length() - 1));
        bufferedWriter.flush();
        bufferedWriter.close();
    }
}
