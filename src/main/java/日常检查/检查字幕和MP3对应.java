package 日常检查;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import 实体类.统一文件接口;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class 检查字幕和MP3对应 implements 统一文件接口 {
    public static void main(String[] args) throws Exception {
        new 检查字幕和MP3对应().process();
    }

    public void handle(File file) {
        File[] files = file.listFiles();
        if (files == null) {
            return;
        }
        List<String> lrcNames = new ArrayList<>();
        List<String> mp3Names = new ArrayList<>();
        for (File file1 : files) {
            String name1 = file1.getName();
            if (name1.endsWith(".lrc")) {
                lrcNames.add(name1.replace(".lrc", ""));
            }
            if (name1.endsWith(".mp3")) {
                mp3Names.add(name1.replace(".mp3", ""));
            }
        }
        List<String> list = ListUtils.removeAll(lrcNames, mp3Names);
        if (CollectionUtils.isNotEmpty(list)) {
            System.out.println(list);
            System.out.println(file.getAbsolutePath());
            return;
        }
        List<String> list1 = ListUtils.removeAll(mp3Names, lrcNames);
        if (CollectionUtils.isNotEmpty(list1)) {
            System.out.println(list1);
            System.out.println(file.getAbsolutePath());
        }
    }
}
