package 统一处理;

import 实体类.统一文件接口;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class LRC统一去除特殊符号 implements 统一文件接口 {
    private static final StringBuilder errorMsg = new StringBuilder("\n----------errorMsg-----------\n");

    public static void main(String[] args) throws Exception {
        new LRC统一去除特殊符号().process();
        System.out.println(errorMsg);
    }

    public void handle(File dir) throws Exception {
        for (File listFile : dir.listFiles()) {
            if (listFile.getName().endsWith(".lrc")) {
                removeChars(listFile);
            }
        }
    }

    private void removeChars(File file) throws Exception {
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
        file.delete();
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getAbsolutePath())));
        bufferedWriter.write(resultStr);
        bufferedWriter.flush();
        bufferedReader.close();
        bufferedWriter.close();
    }

    private boolean endWithInvalidChar(String str) {
        List<String> charList = Arrays.asList(".", "。", "!", "！", ",", "，", "?", "？", ";", "；");
        for (String s : charList) {
            if (str.endsWith(s)) {
                return true;
            }
        }
        return false;
    }
}
