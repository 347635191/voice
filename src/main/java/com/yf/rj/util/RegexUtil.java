package com.yf.rj.util;

import com.yf.rj.common.LrcConstants;
import com.yf.rj.dto.BaseException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RegexUtil {
    private static final String LRC_LINE_REGEX = "\\[\\d{2,3}:\\d{2}.\\d{2}].*";
    private static final String LRC_SPACE_LINE_REGEX = "\\[\\d{2,3}:\\d{2}.\\d{2}] ";
    private static final String[] H_WORDS = new String[]{"对面座位", "后背位", "骑乘位", "正常位", "背面座位",
            "对面立位", "背面立位", "对面侧位", "背面侧位", "屈曲位", "駅弁"};
    private static final String[] ERROR_WORDS = new String[]{"鸡巴", "清扫", "  ", "内射", "wav", "mp3.mp3", ". ", " .", "手枪","序幕","赠品","学长","中粗","正常体位","骑乘体位","欧派","贴身","盖饭"};
    private static final String[] EXCLUDE_WORDS = new String[]{"紧贴身体","口内射精", "腔内射精"};
    private static final String MP3_NAME_REGEX = "\\d{1,3}.(【H】)?[\\u4e00-\\u9fa5\\d A-Z&→%？a-z○αβ=≠\\-+〇≤⇒~×.]+.mp3";
    private static final String RJ_NAME_REGEX = "RJ\\d+ [\\u4e00-\\u9fa5 A-Za-z&×\\d→〇⇒+%x.]+";
    private static final String DL_SITE_SERIES_REGEX = "(?<=<span itemprop=\"name\">「).*?(?=」シリーズ)";

    public static String findFirst(String regex, String text) {
        Matcher matcher = Pattern.compile(regex).matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        return StringUtils.EMPTY;
    }

    public static String findLast(String regex, String text) {
        Matcher matcher = Pattern.compile(regex).matcher(text);
        String last = StringUtils.EMPTY;
        while (matcher.find()) {
            last = matcher.group();
        }
        return last;
    }

    public static void checkLrcLine(String str, String fileName) throws BaseException {
        if (!Pattern.matches(LRC_LINE_REGEX, str)) {
            throw new BaseException(fileName + "字幕格式不正确，" + str);
        }
    }

    public static void checkLrcSpaceLine(String str, String fileName) throws BaseException {
        if (!Pattern.matches(LRC_SPACE_LINE_REGEX, str)) {
            throw new BaseException(fileName + "字幕格式不正确，" + str);
        }
    }

    public static boolean invalidFileName(String name) {
        for (String excludeWord : EXCLUDE_WORDS) {
            name = name.replaceAll(excludeWord, "哦");
        }
        if (StringUtils.containsAny(name, ERROR_WORDS)) {
            return true;
        }
        if (StringUtils.containsAny(name, H_WORDS) && !StringUtils.containsAny(name, "导入", LrcConstants.H_LABEL)) {
            return true;
        }
        return !Pattern.matches(MP3_NAME_REGEX, name);
    }

    public static boolean invalidDirName(String name) {
        return !Pattern.matches(RJ_NAME_REGEX, name);
    }

    /**
     * 获取str前的文本包括str
     */
    public static String last(String text, String str) {
        return RegexUtil.findFirst("^.*" + str, text);
    }

    /**
     * 获取str前的文本,不包括str
     */
    public static String lastBefore(String text, String str) {
        return RegexUtil.findFirst("^.*(?=" + str + ")", text);
    }

    /**
     * 取before和behind中间的文本
     */
    public static String middle(String text, String before, String behind) {
        return RegexUtil.findFirst("(?<=" + before + ").*?(?=" + behind + ")", text);
    }

    public static String getDlSiteSeries(String htmlText) {
        return findFirst(DL_SITE_SERIES_REGEX, htmlText);
    }
}