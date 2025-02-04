package com.yf.rj.util;

import com.yf.rj.common.LrcConstants;
import com.yf.rj.config.WordProperties;
import com.yf.rj.dto.BaseException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * .匹配除换行符以外的所有字符
 * [\s\S]匹配任意字符
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RegexUtil {
    private static final String LRC_LINE_REGEX = "\\[\\d{2,3}:\\d{2}.\\d{2}].*";
    private static final String LRC_SPACE_LINE_REGEX = "\\[\\d{2,3}:\\d{2}.\\d{2}] ";
    private static final String MP3_NAME_REGEX = "\\d{1,3}.(【H】)?[\\u4e00-\\u9fa5\\d A-Z&→%？a-z○αβ=≠\\-+〇≤⇒~×.]+.mp3";
    private static final String RJ_NAME_REGEX = "RJ\\d+ [\\u4e00-\\u9fa5 A-Za-z&×\\d→〇⇒+%x.]+";

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

    public static boolean invalidMp3Name(String name) {
        for (String excludeWord : WordProperties.getExcludeWords()) {
            name = name.replaceAll(excludeWord, "哦");
        }
        if (StringUtils.containsAny(name, WordProperties.getErrorWords())) {
            return true;
        }
        if (StringUtils.containsAny(name, WordProperties.getHWords()) && !StringUtils.containsAny(name, "导入", LrcConstants.H_LABEL)) {
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
}