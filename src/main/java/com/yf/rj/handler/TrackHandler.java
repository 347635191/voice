package com.yf.rj.handler;

import com.yf.rj.config.AsmrOneConfig;
import com.yf.rj.config.DlSiteConfig;
import com.yf.rj.util.RegexUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.text.MessageFormat;

@Component
public class TrackHandler {
    private static final Logger LOG = LoggerFactory.getLogger(TrackHandler.class);

    @Resource
    private RestTemplate proxyTemplate;
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private DlSiteConfig dlSiteConfig;
    @Resource
    private AsmrOneConfig asmrOneConfig;

    public String getSeries(String rj) {
        try {
            String result = proxyTemplate.getForObject(MessageFormat.format(dlSiteConfig.getHtmlUrl(), rj), String.class);
            return RegexUtil.getDlSiteSeries(result);
        } catch (Exception e) {
            LOG.warn("{}爬系列失败", rj, e);
            return "-1";
        }
    }

    public String getWorkInfo(String rj) {
        rj = rj.replaceAll("(?i)rj", StringUtils.EMPTY);
        String url = MessageFormat.format(asmrOneConfig.getWordInfoUrl(), rj);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return response.getBody();
        } catch (Exception e) {
            LOG.warn("RJ{}爬音声信息失败", rj, e);
            return "-1";
        }
    }
}