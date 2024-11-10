package 单独处理;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class 添加待汉化单字幕 {
    private static List<String> mp3List = new ArrayList<>();
    private static List<String> lrcList = new ArrayList<>();

    public static void main(String[] args) {
        outer("E:\\AAA\\sound\\日语\\RJ01274210 受孕炼金工房 身为见习炼金术士的我和 淫乱爆乳女炼金术士×3的受孕造子精炼工房");
    }

    public static void outer(String root) {
        try {
            mp3List.clear();
            lrcList.clear();
            fun(root);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void fun(String root) throws Exception {
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
                bufferedWriter.write("[00:00.00]字幕待汉化\n[99:99.99] ");
                bufferedWriter.flush();
                bufferedWriter.close();
            }
        }
    }
}