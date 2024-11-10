package 统一处理;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import 实体类.统一文件接口;

import java.io.*;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 系列存放到专辑集艺术家里
 */
public class DlSite爬虫标题关键字检索 implements 统一文件接口 {
    private static final String BASE_URL1 = "https://www.dlsite.com/maniax/work/=/product_id/%s.html?locale=zh_CN";
    private static final String BASE_URL2 = "https://www.dlsite.com/girls/work/=/product_id/%s.html/?locale=zh_CN";
    private static final String BASE_URL3 = "https://www.dlsite.com/appx/work/=/product_id/%s.html/?locale=zh_CN";

    private static final String[] BASE_ARR = {BASE_URL1, BASE_URL2, BASE_URL3};

    private static String out = "C:\\Users\\lufeii\\Desktop\\out\\5.txt";

    private static StringBuilder errorMsg = new StringBuilder();

    /**
     * 多个用空格隔开
     */
    private static final String KEY_WORD = "当番 委員";

    public static void main(String[] args) throws Exception {
        Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
        new DlSite爬虫标题关键字检索().process();
        try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out)))) {
            bufferedWriter.write(errorMsg.toString());
            bufferedWriter.flush();
        }
    }

    public void handle(File file) throws IOException {
        //循环获取标题
        String title = loopGetTitle(file.getName(), file.getAbsolutePath(), errorMsg, BASE_ARR);
        if (StringUtils.isBlank(title)) {
            errorMsg.append(file.getAbsolutePath() + "未找到\n");
            return;
        }
        System.out.println(title);
        String[] s = KEY_WORD.split(" ");
        for (String string : s) {
            if (title.contains(string.toLowerCase(Locale.ROOT))) {
                System.out.println(file.getAbsolutePath());
            }
        }
    }

    /**
     * 根据html内容解析出标题
     */
    private static String parseTitleFromHtml(String baseUrl, String rjName) {
        try {
            Document document = Jsoup.connect(String.format(baseUrl, rjName)).get();
            Elements trs = document.getElementsByTag("title");
            if (trs.isEmpty()) {
                return null;
            }
            return trs.get(0).text();
        } catch (Exception e) {
            return null;
        }
    }

    private static String loopGetTitle(String name, String path, StringBuilder errorMsg, String... baseUrl) {
        for (int i = 0; i < baseUrl.length; i++) {
            String xiLie = parseTitleFromHtml(baseUrl[i], name.split(" ")[0]);
            if (StringUtils.isNotBlank(xiLie)) {
                return xiLie;
            }
        }
        return null;
    }
}
