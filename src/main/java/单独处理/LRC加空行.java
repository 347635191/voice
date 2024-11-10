package 单独处理;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.regex.Pattern;

public class LRC加空行 {
    private static String path = "C:\\Users\\lufeii\\Desktop";
    private static String out = "C:\\Users\\lufeii\\Desktop\\out";
    private static StringBuilder preStr = new StringBuilder();
    //跳过的行数
    private static int skipLine = 0;
    private static StringBuilder outStr = new StringBuilder();

    public static void main(String[] args) throws Exception {
        outer(3);
    }

    public static String outer(int skip) {
        skipLine = skip;
        outStr = new StringBuilder();
        preStr = new StringBuilder();
        File file = new File(path);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            String absolutePath = files[i].getAbsolutePath();
            if (absolutePath.endsWith(".lrc") || absolutePath.endsWith(".LRC")) {
                try {
                    addSpace(files[i]);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        preStr.append("\n\n--------------\n\n");
        outStr.append("lrc加空行");
        return preStr.toString() + outStr.toString();
    }

    private static void addSpace(File file) throws Exception {
        //文件有没有超过100行，超过的话时间轴不一致
        boolean flag = false;
        boolean preWord = false;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file.getAbsolutePath())));
        StringBuilder result = new StringBuilder();
        String str;
        for (int i = 0; i < skipLine; i++) {
            str = bufferedReader.readLine();
            System.out.println(str);
            preStr.append(str).append("\n");
        }
        int i = 0;
        while ((str = bufferedReader.readLine()) != null) {
            if (i == 0 && str.charAt(0) == '\uFEFF') {
                str = str.substring(1);
            }
            str = str.trim();
            if (str.length() == 0) {
                System.out.println(file.getName() + "有多余空行");
                if (!StringUtils.contains(outStr, "有多余空行")) {
                    outStr.append(file.getName() + "有多余空行\n");
                }
                continue;
            }
            str = str.replaceFirst("100]", "10]");
            if (!Pattern.matches("\\[\\d{2,3}:\\d{2}.\\d{2}].*", str)) {
                outStr.append("字幕格式不正确\n").append(str).append(file.getName()).append('\n');
                break;
            }
            String time = str.substring(0, 10);
            String word = str.substring(10);
            if (!time.endsWith("]")) {
                flag = true;
                time = str.substring(0, 11);
                word = str.substring(11);
            }
            i++;
            if (word.trim().isEmpty()) {
                if (!StringUtils.contains(outStr, "有时间但空字幕")) {
                    outStr.append(time + file.getName() + "有时间但空字幕\n");
                }
                if (preWord) {
                    result.append(time + " \n");
                    preWord = false;
                } else {
                    outStr.append("字幕格式不正确@@\n").append(str).append(file.getName()).append('\n');
                    break;
                }
            } else {
                if (preWord) {
                    result.append(time + " \n");
                }
                result.append(time + word + '\n');
                preWord = true;
            }
        }
        if (preWord) {
            result.append("[99:99.99] ");
        }
        bufferedReader.close();
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out + "\\\\" + file.getName())));
        if (preWord) {
            bufferedWriter.write(result.toString());
        } else {
            bufferedWriter.write(result.substring(0, result.length() - 1));
        }
        bufferedWriter.flush();
        bufferedWriter.close();
        if (flag) {
            System.out.println(file.getName() + "超过100小时");
            outStr.append(file.getName() + "超过100小时\n");
        }
    }
}