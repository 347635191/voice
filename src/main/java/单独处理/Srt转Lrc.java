package 单独处理;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;

/**
 * [02:50.04]
 * [02:50.04]-(春呼)武哥的~肉棒~
 * [02:50.04]
 * [02:50.04]-(明奈)那麼…學長…
 * 也可以/分割，或者（）包裹
 */
public class Srt转Lrc {
    private static String path = "C:\\Users\\lufeii\\Desktop";
    private static String out = "C:\\Users\\lufeii\\Desktop\\out";
    private static StringBuilder outStr = new StringBuilder();

    public static void main(String[] args) throws Exception {
        File file = new File(path);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            String absolutePath = files[i].getAbsolutePath();
            if (absolutePath.endsWith(".srt") || absolutePath.endsWith(".SRT")) {
                transform(files[i]);
            }
        }
    }

    public static String outer() {
        outStr = new StringBuilder();
        File file = new File(path);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            String absolutePath = files[i].getAbsolutePath();
            if (absolutePath.endsWith(".srt") || absolutePath.endsWith(".SRT")) {
                try {
                    transform(files[i]);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        outStr.append("srt转lrc成功" + RandomStringUtils.randomNumeric(10));
        return outStr.toString();
    }

    private static void transform(File file) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file.getAbsolutePath())));
        String fileName = file.getName().replaceAll(".srt", ".lrc").replaceAll(".SRT", ".lrc");
        String word;
        StringBuilder result = new StringBuilder();
        boolean timeLine = false;
        String startTimeStr = "";
        String endTimeStr = "";
        int index = 0;
        boolean preWord = false;
        boolean needChange = true;
        while ((word = bufferedReader.readLine()) != null) {
            if (word.contains("-->")) {
                if (word.charAt(0) != '0') {
                    outStr.append("超过十小时");
                }
                if (index++ != 0) {
                    result.append(endTimeStr + " \n");
                }
                String start = word.split("-->")[0].trim();
                String end = word.split("-->")[1].trim();
                if (!Pattern.matches("\\d{2}:\\d{2}:\\d{2}.\\d{3}", start)) {
                    outStr.append(file.getName() + start + "开始时间格式不正确\n");
                    needChange = false;
                    break;
                }
                if (!Pattern.matches("\\d{2}:\\d{2}:\\d{2}.\\d{3}", end)) {
                    needChange = false;
                    outStr.append(file.getName() + end + "结束间格式不正确\n");
                    break;
                }
                String startTime = getTime(start);
                String endTime = getTime(end);
                startTimeStr = '[' + startTime + ']';
                endTimeStr = '[' + endTime + ']';
                timeLine = true;
                preWord = false;
            } else {
                if (timeLine && !preWord && StringUtils.isBlank(word)) {
                    needChange = false;
                    outStr.append(file.getName() + startTimeStr + "出现空字幕\n");
                    break;
                }
                if (StringUtils.isBlank(word)) {
                    timeLine = false;
                    preWord = false;
                }
                word = word.replaceAll("\\{\\\\an4}", "");
                word = word.replaceAll("\\{\\\\an5}", "");
                if (timeLine) {
                    if (preWord) {
                        outStr.append(file.getName() + startTimeStr + "有多行字幕\n");
                        result.append(endTimeStr + " \n");
                    }
                    result.append(startTimeStr + word + '\n');
                    preWord = true;
                }
            }
        }
        if (needChange) {
            result.append(endTimeStr + ' ');
            bufferedReader.close();
            file.delete();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get("C:\\Users\\lufeii\\Desktop\\" + fileName))));
            bufferedWriter.write(result.toString());
            bufferedWriter.flush();
            bufferedWriter.close();
        } else {
            bufferedReader.close();
        }
    }

    private static String getTime(String word) {
        return StringUtils.leftPad(String.valueOf(Integer.parseInt(word.substring(1, 2)) * 60 + Integer.parseInt(word.substring(3, 5))), 2, '0') + word.substring(5, 11).replaceAll(",", ".");
    }
}
