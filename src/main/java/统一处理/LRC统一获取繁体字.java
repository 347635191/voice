package 统一处理;

import com.ibm.icu.text.Transliterator;
import 实体类.Word;
import 实体类.统一文件接口;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LRC统一获取繁体字 implements 统一文件接口 {
    public static Set<Word> wordSet = new HashSet<>();

    private static final List<String> ignoreList = Arrays.asList("阪", "跤", "砂", "份", "俱","陽","葉","倉");

    public static void main(String[] args) throws Exception {
        new LRC统一获取繁体字().process();

        for (Word word : wordSet) {
            System.out.println(word);
        }
    }

    public void handle(File dir) throws Exception {
        String dirName = dir.getName();
        String simplifyDirName = simplify(dirName);
        if (!dirName.equals(simplifyDirName)) {
            addRecord(dirName, simplifyDirName);
        }
        for (File listFile : dir.listFiles()) {
            if (listFile.getName().endsWith(".lrc")) {
                collectionFanTi(listFile);
            }
        }
    }

    private void collectionFanTi(File file) throws Exception {
        String name = file.getName();
        String simplifyName = simplify(name);
        if (!name.equals(simplifyName)) {
            addRecord(name, simplifyName);
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file.getAbsolutePath())));
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            String simplify = simplify(str);
            if (!str.equals(simplify)) {
                addRecord(str, simplify);
            }
        }
        bufferedReader.close();
    }

    private static String simplify(String str) {
        str = str.replaceAll("「", "12345678abc");
        str = str.replaceAll("」", "87654321abc");
        Transliterator converter = Transliterator.getInstance("Traditional-Simplified");
        String afterStr = converter.transliterate(str);
        afterStr = afterStr.replaceAll("12345678abc", "「");
        return afterStr.replaceAll("87654321abc", "」");
    }

    private static void addRecord(String str, String simplify) {
        char[] oldCharArr = str.toCharArray();
        char[] newCharArr = simplify.toCharArray();
        for (int i = 0; i < oldCharArr.length; i++) {
            char oldChar = oldCharArr[i];
            char newChar = newCharArr[i];
            if (oldChar != newChar && !ignoreList.contains(String.valueOf(oldChar))) {
                Word word = new Word(String.valueOf(oldChar), String.valueOf(newChar));
                wordSet.add(word);
            }
        }
    }
}
