package 单独处理;

import component.ClipboardUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 〇
 */
public class 手动获取标签 {
    public static void main(String[] args) {
        outer();
    }

    public static void outer() {
        List<String> tagList = new ArrayList<>();
        String tagStr = ClipboardUtils.read();
        if (StringUtils.isBlank(tagStr)) {
            return;
        }
        tagStr = tagStr.replaceAll("大量汁/液", "大量汁液");
        Arrays.stream(tagStr.split(" ")).forEach(tag -> {
            String[] split = tag.split("/");
            for (String s : split) {
                s = s.trim();
                if (StringUtils.equalsIgnoreCase(s, "asmr")) {
                    continue;
                }
                if (!s.isEmpty()) {
                    if (s.contains("（") && s.contains("）")) {
                        s = s.replaceAll("（", "/").replaceAll("）", "");
                        String[] split2 = s.split("/");
                        tagList.addAll(Arrays.asList(split2));
                    } else {
                        tagList.add(s);
                    }
                }
            }
        });
        Collections.sort(tagList);
        ClipboardUtils.write(String.join("/", tagList));
    }
}