package 统一处理;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DlSite爬虫统一设置RJ的艺术家和年份 {
    private static final String BASE_DIR = "E:\\AAA\\sound\\日语\\汉化";

    private static final String BASE_URL1 = "https://www.dlsite.com/maniax/work/=/product_id/%s.html?locale=zh_CN";
    private static final String BASE_URL2 = "https://www.dlsite.com/girls/work/=/product_id/%s.html/?locale=zh_CN";
    private static final String BASE_URL3 = "https://www.dlsite.com/appx/work/=/product_id/%s.html/?locale=zh_CN";

    private static final String[] BASE_ARR = {BASE_URL1, BASE_URL2, BASE_URL3};

    private static String out = "C:\\Users\\lufeii\\Desktop\\out\\4.txt";

    public static void main(String[] args) throws Exception {
        File baseDir = new File(BASE_DIR);
        StringBuilder errorMsg = new StringBuilder();
        for (File file : baseDir.listFiles()) {
            String name = file.getName();
            if (name.equals("封面") || name.equals("desktop.ini")) {
                continue;
            }
            System.out.println(name + "开始处理......");
            //循环获取艺术家
            Pair<String, String> artistAndYear = loopGetArtistAndYear(name, BASE_ARR);
            String artist = artistAndYear.getLeft();
            String year = artistAndYear.getRight();
            if (StringUtils.isAllBlank(artist, year)) {
                errorMsg.append(name + "艺术家和年份都获取失败\n");
                continue;
            }
            if (StringUtils.isBlank(artist)) {
                errorMsg.append(name + "艺术家获取失败\n");
            }
            if (StringUtils.isBlank(year)) {
                errorMsg.append(name + "年份获取失败\n");
            }
            for (File listFile : file.listFiles()) {
                if (listFile.getName().endsWith(".mp3")) {
                    try {
                        setArtistAndYear(listFile, artist, year);
                    } catch (Exception e) {
                        errorMsg.append(name + "写入失败" + "\n");
                        errorMsg.append(listFile.getName() + "写入失败" + "\n");
                        continue;
                    }
                    System.out.println(listFile.getName() + "处理成功");
                }
            }
        }
        try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out)))) {
            bufferedWriter.write(errorMsg.toString());
            bufferedWriter.flush();
        }

    }

    private static void setArtistAndYear(File mp3Source, String artist, String year) throws Exception {
        MP3File mp3File = new MP3File(mp3Source);
        AbstractID3v2Tag id3v2Tag = mp3File.getID3v2Tag();
        if (id3v2Tag == null) {
            throw new RuntimeException("写入失败");
        }
        if (StringUtils.isNoneBlank(artist)) {
            id3v2Tag.setField(FieldKey.ARTIST, artist);
        }
        if (StringUtils.isNoneBlank(year)) {
            id3v2Tag.setField(FieldKey.YEAR, year);
        }
        mp3File.save();
    }

    /**
     * 根据html内容解析出标艺术家
     */
    private static Pair<String, String> parseArtistFromHtml(String baseUrl, String rjName) {
        String artist = null;
        String year = null;
        try {
            List<String> artistList = new ArrayList<>();
            Document document = Jsoup.connect(String.format(baseUrl, rjName)).get();
            Element workOutline = document.getElementById("work_outline");
            Elements trs = workOutline.getElementsByTag("tr");
            for (Element tr : trs) {
                Elements ths = tr.getElementsByTag("th");
                if (ths.size() == 0) {
                    continue;
                }
                Element th = ths.get(0);
                if (th.text().equals("贩卖日") && year == null) {
                    Element td = tr.getElementsByTag("td").get(0);
                    year = td.text().substring(0, 4);
                }
                if (th.text().equals("声优") && artist == null) {
                    Element td = tr.getElementsByTag("td").get(0);
                    Elements as = td.getElementsByTag("中文");
                    for (Element a : as) {
                        String name = a.text().trim();
                        if (StringUtils.equalsAny(name, "琴音有波(紅月ことね)", "紅月ことね")) {
                            name = "琴音有波";
                        }
                        if (StringUtils.equals(name, "乙倉ゅい（乙倉由依）")) {
                            name = "乙倉ゅい";
                        }
                        if (StringUtils.containsAny(name, ".", "。", "(", "（", ")", "）")) {
                            artist = "";
                            continue;
                        }
                        artistList.add(name);
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(artistList) && !StringUtils.equals(artist, "")) {
                Collections.sort(artistList);
                artist = String.join("/", artistList);
            }
            return Pair.of(artist, year);
        } catch (Exception e) {
            return Pair.of(null, null);
        }
    }

    private static Pair<String, String> loopGetArtistAndYear(String name, String... baseUrl) {
        String artist = null;
        String year = null;
        for (String s : baseUrl) {
            Pair<String, String> artistAndYear = parseArtistFromHtml(s, name.split(" ")[0]);
            if (StringUtils.isNotBlank(artistAndYear.getLeft())) {
                artist = artistAndYear.getLeft();
            }
            if (StringUtils.isNotBlank(artistAndYear.getRight())) {
                year = artistAndYear.getRight();
            }
            if (StringUtils.isNoneBlank(artist, year)) {
                return Pair.of(artist, year);
            }
        }
        return Pair.of(artist, year);
    }
}
