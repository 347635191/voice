package 单独处理;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class 添加单字幕 {
    private static List<String> mp3List = new ArrayList<>();
    private static List<String> lrcList = new ArrayList<>();

    public static void main(String[] args) {
        outer("E:\\AAA\\sound\\日语\\RJ01222456 人格交换 在害羞圣女和下流魅魔间反复交换人格 双重快感混乱堕落");
    }

    public static void outer(String root){
        try {
            mp3List.clear();
            lrcList.clear();
            fun(root);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void fun(String root) throws Exception{
        File file = new File(root);
        for (File listFile : file.listFiles()) {
            String listFileName = listFile.getName();
            if (listFileName.endsWith(".mp3")) {
                mp3List.add(listFileName.replaceAll(".mp3", ""));
            }
            if (listFileName.endsWith(".lrc")) {
                lrcList.add(listFileName.replaceAll(".lrc", ""));
            }
        }
        for (String mp3Name : mp3List) {
            if (!lrcList.contains(mp3Name)) {
//                File newFile = new File(root + "\\\\" + mp3Name + ".lrc");
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(root + "\\\\" + mp3Name + ".lrc")));
                bufferedWriter.write("[00:00.00]" + mp3Name.replaceAll("【H】", "").split("\\.", 2)[1] + "\n[99:99.99] ");
                bufferedWriter.flush();
                bufferedWriter.close();
            }
        }
    }
}