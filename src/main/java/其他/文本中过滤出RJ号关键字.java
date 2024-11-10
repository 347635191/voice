package 其他;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class 文本中过滤出RJ号关键字 {
    private static String path1 = "C:\\Users\\lufeii\\Desktop\\aaa.txt";
    private static String path2 = "C:\\Users\\lufeii\\Desktop\\aaa1.txt";

    public static void main(String[] args) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path1)));
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path2)));

        String str;
        while ((str = bufferedReader.readLine()) != null) {
            Pattern pattern = Pattern.compile("RJ(\\d)+", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(str);
            while(matcher.find()){
                bufferedWriter.write(matcher.group() + '\n');
                bufferedWriter.flush();
            }
        }
        bufferedReader.close();
        bufferedWriter.close();
    }
}