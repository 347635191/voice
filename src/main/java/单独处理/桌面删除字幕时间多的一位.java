package 单独处理;

import java.io.*;

public class 桌面删除字幕时间多的一位 {
    private static String path = "C:\\Users\\lufeii\\Desktop";
    private static String out = "C:\\Users\\lufeii\\Desktop\\out";

    public static void main(String[] args) throws Exception {
        File file = new File(path);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            String absolutePath = files[i].getAbsolutePath();
            if (absolutePath.endsWith(".lrc") || absolutePath.endsWith(".LRC")) {
                deleteChar(files[i]);
            }
        }
    }

    private static void deleteChar(File file) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file.getAbsolutePath())));
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out + "\\\\" + file.getName())));
        String str;
        StringBuilder result = new StringBuilder();
        while ((str = bufferedReader.readLine()) != null) {
            str = str.trim();
            if (str.charAt(0) == '\uFEFF') {
                System.out.println("编码格式不对：" + file.getName());
                str = str.substring(1);
            }
            if(str.charAt(9)!= ']') {
                str = str.substring(0, 9) + str.substring(10);
            }
            result.append(str + '\n');
        }
        bufferedWriter.write(result.substring(0, result.length() - 1));
        bufferedWriter.flush();
        bufferedReader.close();
        bufferedWriter.close();
    }
}
