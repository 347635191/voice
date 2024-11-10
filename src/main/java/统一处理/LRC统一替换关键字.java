package 统一处理;

import 实体类.统一文件接口;

import java.io.*;

/**
 * 建议先调用Lrc统一获取繁体字
 * 然后再根据关键字确认一下
 * <p>
 * 下列需手动替换
 * 妳 你
 * 噁 恶
 * 撫 抚
 * //啰 咯
 * //阳向葵 陽向葵（还原）
 */
public class LRC统一替换关键字 implements 统一文件接口 {
    private static final String KEY_WORD = "揉著";
    private static final String REPLACEMENT = "揉着";

    public static void main(String[] args) throws Exception {
        new LRC统一替换关键字().process();
    }

    public void handle(File dir) throws Exception {
        for (File listFile : dir.listFiles()) {
            if (listFile.getName().endsWith(".lrc")) {
                replaceKeyWord(listFile);
            }
        }
    }

    private void replaceKeyWord(File file) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file.getAbsolutePath())));
        StringBuilder result = new StringBuilder();
        String str;
        boolean flag = false; //是否匹配到关键字
        while ((str = bufferedReader.readLine()) != null) {
            if (str.toLowerCase().contains(KEY_WORD)) {
                flag = true;
                System.out.println(str);
                str = str.replaceAll(KEY_WORD, REPLACEMENT);
            }
            result.append(str + '\n');
        }
        if (flag) {
            System.out.println(file.getAbsolutePath());
            String resultStr = result.toString();
            if (resultStr.endsWith("\n")) {
                resultStr = resultStr.substring(0, resultStr.length() - 1);
            }
            file.delete();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getAbsolutePath())));
            bufferedWriter.write(resultStr);
            bufferedWriter.flush();
            bufferedWriter.close();
        }
        bufferedReader.close();
    }
}
