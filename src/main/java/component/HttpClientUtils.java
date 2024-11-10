package component;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpClientUtils {
    public static String getRjInfo(String rj) {
        if (StringUtils.isBlank(rj)) {
            return "";
        }
        HttpClient httpClient = HttpClients.createDefault();
        rj = rj.replaceAll("RJ", "");
        HttpGet httpGet = new HttpGet("https://api.asmr-200.com/api/workInfo/" + rj);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            return "";
        }
    }

    public static String getXiLie(String rj) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpHost proxy = new HttpHost("127.0.0.1", 7890);
        RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
        HttpGet httpGet = new HttpGet("https://www.dlsite.com/maniax/work/=/product_id/" + rj + ".html");
        httpGet.setConfig(config);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            String result = EntityUtils.toString(response.getEntity());
            String regex = "(?<=<span itemprop=\"name\">「).*?(?=」シリーズ)";
            Matcher matcher = Pattern.compile(regex).matcher(result);
            while (matcher.find()) {
                return matcher.group();
            }
            return "";
        } catch (Exception e) {
            return "-1";
        }
    }
}