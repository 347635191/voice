package com.yf.rj.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 正则表达式解析html文档
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DlSiteRegexUtil {
    private static final String DL_SITE_SERIES_REGEX = "(?<=シリーズ名[\\s\\S]{1,200}titles\">)[\\s\\S]*?(?=</a></td>)";
    private static final String DL_SITE_COMPOSER_REGEX = "(?<=サークル名[\\s\\S]{1,200}html\">)[\\s\\S]*?(?=</a>)";
    private static final String DL_SITE_YEAR_REGEX = "(?<=販売日[\\s\\S]{1,200}\">)[\\s\\S]*?(?=年)";

    /**
     * 从dlSite的html里获取系列
     */
    public static String getDlSiteSeries(String htmlText) {
        return RegexUtil.findFirst(DL_SITE_SERIES_REGEX, htmlText);
    }

    /**
     * 从dlSite的html里获取社团
     */
    public static String getDlSiteComposer(String htmlText) {
        return  RegexUtil.findFirst(DL_SITE_COMPOSER_REGEX, htmlText);
    }

    /**
     * 从dlSite的html里获取年份
     */
    public static String getDlSiteYear(String htmlText) {
        return  RegexUtil.findFirst(DL_SITE_YEAR_REGEX, htmlText);
    }
}