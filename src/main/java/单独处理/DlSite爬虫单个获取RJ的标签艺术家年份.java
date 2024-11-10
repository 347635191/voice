package 单独处理;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import 实体类.SoundItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class DlSite爬虫单个获取RJ的标签艺术家年份 {
    private static final String BASE_URL1 = "https://www.dlsite.com/maniax/work/=/product_id/%s.html/?locale=zh_CN";
    private static final String BASE_URL2 = "https://www.dlsite.com/girls/work/=/product_id/%s.html/?locale=zh_CN";
    private static final String BASE_URL3 = "https://www.dlsite.com/appx/work/=/product_id/%s.html/?locale=zh_CN";

    private static final String[] BASE_ARR = {BASE_URL1, BASE_URL2, BASE_URL3};

    private static String regex = "[\\u4e00-\\u9fa5A-Za-z/]+";

    private static String RJ_NAME = "RJ01037944";

    public static void main(String[] args) throws Exception {
        SoundItem soundItem = loopGetTag(RJ_NAME, BASE_ARR);
        System.out.println(soundItem.getArtist());
        System.out.println(soundItem.getYear());
        System.out.println(soundItem.getTag());
        System.out.println(soundItem.getXiLie());
    }

    /**
     * 根据html内容解析出标签
     */
    private static SoundItem parseSoundFromHtml(String baseUrl, String rjName) {
        SoundItem soundItem = new SoundItem();
        rjName = rjName.trim();
        try {
            List<String> tagList = new ArrayList<>();
            List<String> artistList = new ArrayList<>();
            Document document = Jsoup.connect(String.format(baseUrl, rjName)).get();
            Element workOutline = document.getElementById("work_outline");
            Element mainGenre = workOutline.getElementsByClass("main_genre").first();
            Elements as = mainGenre.getElementsByTag("中文");
            for (Element a : as) {
                String[] split = a.text().replaceAll("大量汁/液", "大量汁液").split("/");
                for (String s : split) {
                    s = s.trim();
                    if (StringUtils.equalsIgnoreCase(s, "asmr")) {
                        continue;
                    }
                    if (s.length() != 0) {
                        if (s.contains("（") && s.contains("）")) {
                            s = s.replaceAll("（", "/").replaceAll("）", "");
                            String[] split2 = s.split("/");
                            tagList.addAll(Arrays.asList(split2));
                        } else {
                            tagList.add(s);
                        }
                    }
                }
            }
            Collections.sort(tagList);
            soundItem.setTag(String.join("/", tagList));


            Elements trs = workOutline.getElementsByTag("tr");
            for (Element tr : trs) {
                Elements ths = tr.getElementsByTag("th");
                if (ths.size() == 0) {
                    continue;
                }
                Element th = ths.get(0);
                if (th.text().equals("贩卖日") && StringUtils.isBlank(soundItem.getYear())) {
                    Element td = tr.getElementsByTag("td").get(0);
                    soundItem.setYear(td.text().substring(0, 4));
                }
                if (th.text().equals("声优") && StringUtils.isBlank(soundItem.getArtist())) {
                    Element td = tr.getElementsByTag("td").get(0);
                    Elements ass = td.getElementsByTag("中文");
                    for (Element a : ass) {
                        String name = a.text().trim();
                        if (StringUtils.equalsAny(name, "琴音有波(紅月ことね)", "紅月ことね")) {
                            name = "琴音有波";
                        }
                        if (StringUtils.equals(name, "乙倉ゅい（乙倉由依）")) {
                            name = "乙倉ゅい";
                        }
                        artistList.add(name);
                    }
                }
                if (th.text().equals("系列名") && StringUtils.isBlank(soundItem.getXiLie())) {
                    Element td = tr.getElementsByTag("td").get(0);
                    Element a = td.getElementsByTag("中文").get(0);
                    soundItem.setXiLie(a.text());
                }
            }
            if (CollectionUtils.isNotEmpty(artistList)) {
                Collections.sort(artistList);
                soundItem.setArtist(String.join("/", artistList));
            }
        } catch (Exception e) {
            return null;
        }
        return soundItem;
    }


    private static SoundItem loopGetTag(String rjName, String... baseUrl) {
        SoundItem result = new SoundItem();
        for (String s : baseUrl) {
            SoundItem soundItem = parseSoundFromHtml(s, rjName);
            if (soundItem == null) {
                continue;
            }
            String tag = soundItem.getTag();
            String artist = soundItem.getArtist();
            String year = soundItem.getYear();
            String xiLie = soundItem.getXiLie();
            if (StringUtils.isNotBlank(tag) && Pattern.matches(regex, tag)) {
                result.setTag(tag);
            }
            if (StringUtils.isNotBlank(artist)) {
                result.setArtist(artist);
            }
            if (StringUtils.isNotBlank(year)) {
                result.setYear(year);
            }
            if (StringUtils.isNotBlank(xiLie)) {
                result.setXiLie(xiLie);
            }
            if (StringUtils.isNoneBlank(result.getTag(), result.getArtist(), result.getYear())) {
                return result;
            }
        }
        return result;
    }
}
