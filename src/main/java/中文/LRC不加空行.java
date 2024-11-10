package 中文;

import org.apache.commons.lang3.StringUtils;

import java.io.*;

/**
 * 删除空行
 */
public class LRC不加空行 {
    private static String path = "C:\\Users\\lufeii\\Desktop";
    private static String out = "C:\\Users\\lufeii\\Desktop\\out";
    private static StringBuilder errorMsg = new StringBuilder();
    //跳过的行数
    private static int skipLine = 0;

    public static void main(String[] args) throws Exception {
        File file = new File(path);
        File[] files = file.listFiles();
        for (File value : files) {
            String absolutePath = value.getAbsolutePath();
            if (absolutePath.endsWith(".lrc") || absolutePath.endsWith(".LRC")) {
                addSpace(value);
            }
        }
        System.out.println("-------------------------------------");
        System.out.println("-------------------------------------");
        System.out.println(errorMsg);
    }

    private static void addSpace(File file) throws Exception {
        //文件有没有超过100行，超过的话时间轴不一致
        boolean flag = false;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file.getAbsolutePath())));
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out + "\\\\" + file.getName())));
        String str;
//        for (int i = 0; i < skipLine; i++) {
//            str = bufferedReader.readLine();
//            System.out.println(str);
//        }
//        str = bufferedReader.readLine();
        StringBuilder result = new StringBuilder();
//        String word = str.substring(10);
//        result.append("[00:00.00]").append(word).append('\n');
//        if(StringUtils.isBlank(word)){
//            throw new RuntimeException(file.getName() + "第一行为空");
//        }

        while ((str = bufferedReader.readLine()) != null) {
            if (!str.contains("[")) {
                continue;
            }
            if (StringUtils.isBlank(str)) {
                continue;
            }
            if (str.charAt(0) == '\uFEFF') {
                str = str.substring(1);
            }
            if (StringUtils.isBlank(str)) {
                continue;
            }
            int index = 10;
            if (StringUtils.substring(str, 10, 11).equals("]")) {
                System.out.println(str);
                flag = true;
                index = 11;
            }
            if (str.length() < 10) {
                System.out.println(str);
                System.out.println(file.getAbsolutePath());
            }
            String subString = str.substring(index);
            if (!subString.trim().isEmpty()) {
                str = str.substring(0, 9) + str.substring(10);
                result.append(str).append('\n');
            }
        }
        bufferedWriter.write(result.substring(0, result.length() - 1));
        bufferedWriter.flush();
        bufferedReader.close();
        bufferedWriter.close();
        if (flag) {
            errorMsg.append(file.getName() + "超过100小时\n");
        }
    }
}
