package 单独处理;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class 桌面字幕去掉特殊符号 {
    private static String path = "C:\\Users\\lufeii\\Desktop";
    private static String out = "C:\\Users\\lufeii\\Desktop\\out";
    private static StringBuilder errorMsg = new StringBuilder("\n----------errorMsg-----------\n");
    private static StringBuilder outStr = new StringBuilder();

    public static void main(String[] args) throws Exception {
        outer();
//        File file = new File(path);
//        File[] files = file.listFiles();
//        for (int i = 0; i < files.length; i++) {
//            String absolutePath = files[i].getAbsolutePath();
//            if (absolutePath.endsWith(".lrc") || absolutePath.endsWith(".LRC")) {
//                removeChars(files[i]);
//            }
//        }
    }

    public static String outer() {
        outStr = new StringBuilder();
        errorMsg = new StringBuilder();
        File file = new File(path);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            String absolutePath = files[i].getAbsolutePath();
            if (absolutePath.endsWith(".lrc") || absolutePath.endsWith(".LRC")) {
                try {
                    removeChars(files[i]);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        outStr.append(errorMsg);
        outStr.append("去特殊符号成功");
        return outStr.toString();
    }

    private static void removeChars(File file) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file.getAbsolutePath())));
        String str;
        int index = 1;
        StringBuilder result = new StringBuilder();
        //判断文件是否要改动，如果有空行先不改，等检查完再改
        boolean reWriteFlag = true;
        while ((str = bufferedReader.readLine()) != null) {
            if (str.trim().isEmpty()) {
                reWriteFlag = false;
                errorMsg.append(file.getAbsolutePath() + "有多余空行，先不改动\n");
                break;
            }
            str = str.replaceAll("\uFE0F", "");
            if (index % 2 == 0) {
                result.append(str + '\n');
            } else {
                int point;
                if (Pattern.matches("\\[\\d{2}:\\d{2}.\\d{2}].*", str)) {
                    point = 10;
                } else if (Pattern.matches("\\[\\d{3}:\\d{2}.\\d{2}].*", str)) {
                    point = 11;
                } else {
                    reWriteFlag = false;
                    errorMsg.append(file.getAbsolutePath() + "时间格式不正确，先不改动\n");
                    break;
                }
                String str1 = str.substring(0, point);
                String str2 = str.substring(point);
                if (str2.trim().isEmpty()) {
                    bufferedReader.readLine();
                    continue;
                }
                String preStr = str2;
                str2 = str2.trim().replaceAll("　", " ");
                str2 = str2.trim().replaceAll("[.]{1,}", " ");
                str2 = str2.trim().replaceAll("[。]{1,}", " ");
                str2 = str2.trim().replaceAll("[…]{1,}", " ");
                str2 = str2.trim().replaceAll("[,]{1,}", " ");
                str2 = str2.trim().replaceAll("[，]{1,}", " ");
                str2 = str2.trim().replaceAll("[ ]{2,}", " ");
                str2 = str2.trim().replaceAll("[·]{1,}", " ");
                str2 = str2.trim().replaceAll("[~]{1,}", " ");
                str2 = str2.trim().replaceAll("[、]{1,}", " ");
                str2 = str2.trim().replaceAll("[—]{1,}", " ");
                str2 = str2.trim().replaceAll("[－]{1,}", " ");
                str2 = str2.trim().replaceAll("[～]{1,}", " ");
                str2 = str2.trim().replaceAll("[〜]{1,}", " ");
                str2 = str2.trim().replaceAll("[♡]{1,}", " ");
                str2 = str2.trim().replaceAll("[❤]{1,}", " ");
                str2 = str2.trim().replaceAll("[♥]{1,}", " ");
                str2 = str2.trim().replaceAll("[❥]{1,}", " ");
                str2 = str2.trim().replaceAll("[♪]{1,}", " ");
                str2 = str2.trim().replaceAll("[!]{1,}", " ");
                str2 = str2.trim().replaceAll("[！]{1,}", " ");
                str2 = str2.trim().replaceAll("[？]{1,}", " ");
                str2 = str2.trim().replaceAll("[?]{1,}", " ");
                str2 = str2.trim().replaceAll("[⁉]{1,}", " ");
                str2 = str2.trim().replaceAll("-", " ");
                str2 = str2.trim().replaceAll("☆", " ");
                str2 = str2.trim().replaceAll(" \\)", ")");
                str2 = str2.trim().replaceAll(" ）", "）");
                str2 = str2.trim().replaceAll(" ]", "]");
                str2 = str2.trim().replaceAll(" 】", "】");
                str2 = str2.trim().replaceAll(" 」", "」");
                str2 = str2.trim().replaceAll(" 』", "』");
                str2 = str2.trim().replaceAll("\\( ", "(");
                str2 = str2.trim().replaceAll("（ ", "（");
                str2 = str2.trim().replaceAll("\\[ ", "[");
                str2 = str2.trim().replaceAll("【 ", "【");
                str2 = str2.trim().replaceAll("「 ", "「");
                str2 = str2.trim().replaceAll("『 ", "『");
                str2 = str2.trim().replaceAll("・", " ");
                str2 = str2.trim().replaceAll("\t", " ");
                if (endWithInvalidChar(str2)) {
                    str2 = str2.substring(0, str2.length() - 1);
                }
                if (endWithInvalidChar(str2)) {
                    str2 = str2.substring(0, str2.length() - 1);
                }
                if (endWithInvalidChar(str2)) {
                    str2 = str2.substring(0, str2.length() - 1);
                }
                if (endWithInvalidChar(str2)) {
                    str2 = str2.substring(0, str2.length() - 1);
                }
                if (!preStr.equals(str2)) {
                    System.out.println(preStr);
                    System.out.println(str2);
                    outStr.append(preStr).append("\n").append(str2).append("\n");
                }
                result.append(str1.trim() + str2.trim() + '\n');
            }
            index++;
        }
        if (!reWriteFlag) {
            return;
        }
        String resultStr = result.toString();
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