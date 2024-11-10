package 单独处理;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.regex.Pattern;

/**
 * 删除空行
 */
public class LRC不加空行 {
    private static String path = "C:\\Users\\lufeii\\Desktop";
    private static String out = "C:\\Users\\lufeii\\Desktop\\out";
    private static StringBuilder errorMsg = new StringBuilder();
    //跳过的行数
    private static int skipLine = 1;
    private static StringBuilder outStr = new StringBuilder();

    public static void main(String[] args) throws Exception {
        File file = new File(path);
        File[] files = file.listFiles();
        for (File value : files) {
            String absolutePath = value.getAbsolutePath();
            if (absolutePath.endsWith(".lrc") || absolutePath.endsWith(".LRC")) {
                addSpace(value, skipLine);
            }
        }
        System.out.println(errorMsg);
    }

    public static String outer(int skipLine) {
        outStr = new StringBuilder();
        errorMsg = new StringBuilder();
        File file = new File(path);
        File[] files = file.listFiles();
        for (File value : files) {
            String absolutePath = value.getAbsolutePath();
            if (absolutePath.endsWith(".lrc") || absolutePath.endsWith(".LRC")) {
                try {
                    addSpace(value, skipLine);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        outStr.append("\n\n--------------\n\n");
        outStr.append(errorMsg);
        outStr.append("lrc不加空行成功");
        return outStr.toString();
    }

    private static void addSpace(File file, int skipLine) throws Exception {
        //文件有没有超过100行，超过的话时间轴不一致
        boolean flag = false;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file.getAbsolutePath())));
        String str;
        for (int i = 0; i < skipLine; i++) {
            str = bufferedReader.readLine();
            System.out.println(str);
            outStr.append(str).append("\n");
        }
        StringBuilder result = new StringBuilder();
        int i = 0;
        while ((str = bufferedReader.readLine()) != null) {
            if (i == 0 && str.charAt(0) == '\uFEFF') {
                str = str.substring(1);
            }
            if (StringUtils.isBlank(str)) {
                continue;
            }
            str = str.replaceFirst("100]", "10]");
            int index = str.indexOf("]") + 1;
            if (index != 10) {
                flag = true;
            }
            String subString = str.substring(index);
            if (i % 2 == 1) {
                //偶数行
                if (StringUtils.isBlank(subString)) {
                    str = str.trim() + " ";
                    subString = " ";
                }
            }
            if (!Pattern.matches("\\[\\d{2,3}:\\d{2}.\\d{2}].+", str)) {
                errorMsg.append("字幕格式不正确\n").append(str).append(file.getName()).append('\n');
            }
            if (i % 2 == 1) {
                if (!StringUtils.equals(subString, " ") && !StringUtils.contains(outStr, "字幕偶数行不为空格")) {
                    errorMsg.append(file.getName() + "字幕偶数行不为空格:" + str);
                }
            } else {
                if (StringUtils.isBlank(subString) && !StringUtils.contains(outStr, "字幕奇数行为空")) {
                    errorMsg.append(file.getName() + "字幕奇数行为空:" + str);
                }
            }
            i++;
            result.append(str).append('\n');
        }
        if (i % 2 != 0) {
            errorMsg.append(file.getName() + "最后一行有字");
        }
        if (result.length() <= 1) {
            errorMsg.append(file.getName() + "为空字幕");
            bufferedReader.close();
        } else {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out + "\\\\" + file.getName())));
            bufferedWriter.write(result.substring(0, result.length() - 1));
            bufferedWriter.flush();
            bufferedReader.close();
            bufferedWriter.close();
        }
        bufferedReader.close();
        if (flag) {
            if (!StringUtils.contains(errorMsg, "超过100小时")) {
                errorMsg.append(file.getName() + "超过100小时\n");
            }
        }
    }
}
