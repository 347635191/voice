package 单独处理;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class 桌面字幕第七位换成点号 {
    private static String path = "C:\\Users\\lufeii\\Desktop";
    private static String out = "C:\\Users\\lufeii\\Desktop\\out";
    private static final StringBuilder errorMsg = new StringBuilder("\n----------errorMsg-----------\n");


    public static void main(String[] args) throws Exception {
        File file = new File(path);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            String absolutePath = files[i].getAbsolutePath();
            if (absolutePath.endsWith(".lrc") || absolutePath.endsWith(".LRC")) {
                removeChars(files[i]);
            }
        }
    }

    private static void removeChars(File file) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file.getAbsolutePath())));
        String str;
        StringBuilder sb = new StringBuilder();
        while ((str = bufferedReader.readLine()) != null) {
           if(str.charAt(6) == ':'){
               str = str.substring(0,6) + "." + str.substring(7);
           }
            sb.append(str + '\n');
        }
        String resultStr = sb.toString();
        if (resultStr.endsWith("\n")) {
            resultStr = resultStr.substring(0, resultStr.length() - 1);
        }
        bufferedReader.close();
        file.delete();
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getAbsolutePath())));
        bufferedWriter.write(resultStr);
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    private static boolean endWithInvalidChar(String str) {
        List<String> charList = Arrays.asList(".", "。", "!", "！", ",", "，", "?", "？", ";", "；");
        for (String s : charList) {
            if (str.endsWith(s)) {
                return true;
            }
        }
        return false;
    }
}
