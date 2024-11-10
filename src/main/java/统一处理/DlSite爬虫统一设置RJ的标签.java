package 统一处理;

import org.apache.commons.lang3.StringUtils;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class DlSite爬虫统一设置RJ的标签 {
    private static final String BASE_DIR = "E:\\AAA\\sound\\日语\\汉化";

    private static final String BASE_URL1 = "https://www.dlsite.com/maniax/work/=/product_id/%s.html?locale=zh_CN";
    private static final String BASE_URL2 = "https://www.dlsite.com/girls/work/=/product_id/%s.html/?locale=zh_CN";
    private static final String BASE_URL3 = "https://www.dlsite.com/appx/work/=/product_id/%s.html/?locale=zh_CN";

    private static final String[] BASE_ARR = {BASE_URL1, BASE_URL2, BASE_URL3};

    private static String out = "C:\\Users\\lufeii\\Desktop\\out\\4.txt";

    private static String regex = "[\\u4e00-\\u9fa5A-Za-z/]+";

    public static void main(String[] args) throws Exception {
        File baseDir = new File(BASE_DIR);
        StringBuilder errorMsg = new StringBuilder();
        for (File file : baseDir.listFiles()) {
            String name = file.getName();
            if (name.equals("封面") || name.equals("desktop.ini")) {
                continue;
            }
            System.out.println(name + "开始处理......");
            //循环获取标签
            String tag = loopGetTag(name, errorMsg, BASE_ARR);
            if (StringUtils.isBlank(tag)) {
                continue;
            }
            for (File listFile : file.listFiles()) {
                if (listFile.getName().endsWith(".mp3")) {
//                    ID3v2 id3v2Tag = mp3File.getId3v2Tag();
//                    if (StringUtils.isNotBlank(id3v2Tag.getComment())) {
//                        System.out.println(listFile.getName() + "已处理过，不再处理");
//                        continue;
//                    }
                    try {
                        setComment(listFile, tag);
                    } catch (Exception e) {
                        errorMsg.append(name + "写入标签失败" + "\n");
                        errorMsg.append(listFile.getName() + "写入标签失败" + "\n");
                        continue;
                    }
                    System.out.println(listFile.getName() + "处理成功");
                }
            }
        }
        try (
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out)))) {
            bufferedWriter.write(errorMsg.toString());
            bufferedWriter.flush();
        }

    }

    private static void setComment(File mp3Source, String tag) throws Exception {
        MP3File mp3File = new MP3File(mp3Source);
        AbstractID3v2Tag id3v2Tag = mp3File.getID3v2Tag();
        if (id3v2Tag == null) {
            throw new RuntimeException("写入失败");
        }
        id3v2Tag.setField(FieldKey.COMMENT, tag);
        mp3File.save();
    }

    /**
     * 根据html内容解析出标签
     */
    private static String parseTagFromHtml(String baseUrl, String rjName) {
        try {
            List<String> tagList = new ArrayList<>();
            Document document = Jsoup.connect(String.format(baseUrl, rjName)).get();
            Element workOutline = document.getElementById("work_outline");
            Element mainGenre = workOutline.getElementsByClass("main_genre").first();
            Elements as = mainGenre.getElementsByTag("中文");
            for (Element a : as) {
                String[] split = a.text().split("/");
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
            document = null;
            Collections.sort(tagList);
            return String.join("/", tagList);
        } catch (Exception e) {
            return null;
        }
    }

    private static String loopGetTag(String name, StringBuilder errorMsg, String... baseUrl) {
        for (int i = 0; i < baseUrl.length; i++) {
            String tag = parseTagFromHtml(baseUrl[i], name.split(" ")[0]);
            if (StringUtils.isBlank(tag)) {
                errorMsg.append(name + "标签第" + i + "次未找到\n");
            } else if (!Pattern.matches(regex, tag)) {
                errorMsg.append(name + "标签第" + i + "次有非法字符\n");
            } else {
                if (i != 0) {
                    errorMsg.append(name + "标签第" + i + "次处理成功\n");
                }
                return tag;
            }
        }
        return null;
    }
}
