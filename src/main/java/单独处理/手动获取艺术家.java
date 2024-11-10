package 单独处理;

import component.ClipboardUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class 手动获取艺术家 {
    public static void main(String[] args) {
        outer();
    }
    public static void outer() {
        List<String> artistList = new ArrayList<>();
        String artistStr = ClipboardUtils.read();
        artistStr = artistStr.trim();
        List<String> collect = Arrays.stream(artistStr.split("/")).map(String::trim).collect(Collectors.toList());
        for (String name : collect) {
            if (StringUtils.equalsAny(name, "琴音有波(紅月ことね)", "紅月ことね")) {
                name = "琴音有波";
            }
            if (StringUtils.equalsAny(name, "乙倉ゅい（乙倉由依）", "乙倉ゅい(乙倉由依)")) {
                name = "乙倉ゅい";
            }
            artistList.add(name);
        }
        Collections.sort(artistList);
        ClipboardUtils.write(String.join("/", artistList));
    }
}