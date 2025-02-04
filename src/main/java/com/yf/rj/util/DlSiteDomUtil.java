package com.yf.rj.util;

import com.yf.rj.common.SymbolConstants;
import com.yf.rj.entity.Mp3T;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * jsoup解析html文档
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DlSiteDomUtil {
    private static final String DATE_KEY = "贩卖日";
    private static final String SERIES_KEY = "系列名";
    private static final String ARTIST_KEY = "声优";
    private static final String COMMENT_KEY = "分类";

    /**
     * 从dlSite的html里获取音频属性
     */
    public static Mp3T getDlSiteAttr(String htmlText) {
        Document document = Jsoup.parse(htmlText);
        Element workMaker = document.getElementById("work_maker");
        Element workOutline = document.getElementById("work_outline");
        if (workMaker == null || workOutline == null) {
            return null;
        }
        Mp3T mp3T = new Mp3T();
        mp3T.setComposer(workMaker.select("a").text());
        Elements attrs = workOutline.select("tr");
        attrs.forEach(attr -> {
            String attrKey = attr.select("th").get(0).text();
            switch (attrKey) {
                case DATE_KEY:
                    mp3T.setYear(findYear(attr));
                    break;
                case SERIES_KEY:
                    mp3T.setSeries(findSeries(attr));
                    break;
                case ARTIST_KEY:
                    mp3T.setArtist(findArtist(attr));
                    break;
                case COMMENT_KEY:
                    mp3T.setComment(findComment(attr));
                    break;
                default:
                    break;
            }
        });
        return mp3T;
    }

    private static String findYear(Element attr) {
        String date = attr.select("a").text();
        String splitWord = "年";
        if (StringUtils.contains(date, splitWord)) {
            return date.split(splitWord)[0];
        }
        return null;
    }

    private static String findSeries(Element attr) {
        return attr.select("a").text();
    }

    private static String findArtist(Element attr) {
        String words = attr.select("a").stream().map(Element::text)
                .collect(Collectors.joining(SymbolConstants.ARTIST_DELIMITER));
        return formatArtist(words);
    }

    private static String findComment(Element attr) {
        String words = attr.select("a").stream().map(Element::text)
                .collect(Collectors.joining(StringUtils.SPACE));
        return formatComment(words);
    }

    public static String formatArtist(String words) {
        words = words.replaceAll("(琴音有波\\(紅月ことね\\))|(紅月ことね)"
                , "琴音有波").replaceAll("(乙倉ゅい（乙倉由依）)|(乙倉ゅい\\(乙倉由依\\))", "乙倉ゅい");
        return Arrays.stream(words.split(SymbolConstants.ARTIST_DELIMITER))
                .map(StringUtils::trim).sorted()
                .collect(Collectors.joining(SymbolConstants.ARTIST_DELIMITER));
    }

    public static String formatComment(String words) {
        words = words.replaceAll("大量汁/液", "大量汁液");
        return Arrays.stream(words.split(StringUtils.SPACE)).map(StringUtils::trim)
                .map(word -> {
                    word = word.replaceAll("[（(]", SymbolConstants.COMMENT_DELIMITER);
                    word = word.replaceAll("[）)]", "");
                    return word.split(SymbolConstants.COMMENT_DELIMITER);
                }).flatMap(Arrays::stream).map(StringUtils::trim).sorted()
                .collect(Collectors.joining(SymbolConstants.COMMENT_DELIMITER));
    }
}