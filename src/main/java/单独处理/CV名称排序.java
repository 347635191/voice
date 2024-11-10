package 单独处理;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

//紅月ことね->琴音有波
public class CV名称排序 {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("かの仔");
        list.add("沢野ぽぷら");
        list.add("そらまめ");
        list.add("みもりあいの");
        list = list.stream().map(name -> name.replaceAll("。", "")).collect(Collectors.toList());
        Collections.sort(list);
        System.out.println(String.join("/", list));
    }
}
