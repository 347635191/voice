package com.yf.rj.util;

import com.ibm.icu.text.Transliterator;
import com.yf.rj.dto.BaseException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * 文本工具类
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WordUtil {
    public static String simplify(String str) {
        str = str.replaceAll("妳", "你");
        str = str.replaceAll("噁", "恶");
        str = str.replaceAll("拚", "拼");
        str = str.replaceAll("沒", "没");
        str = str.replaceAll("吶", "呐");
        str = str.replaceAll("睪", "睾");
        str = str.replaceAll("麼", "么");

        str = str.replaceAll("「", "1234a5678");
        str = str.replaceAll("」", "8765a4321");
        Transliterator converter = Transliterator.getInstance("Traditional-Simplified");
        String afterStr = converter.transliterate(str);
        afterStr = afterStr.replaceAll("1234a5678", "「");
        return afterStr.replaceAll("8765a4321", "」");
    }

    public static String getTime(String word) throws BaseException {
        if (!Pattern.matches("\\d{2}:\\d{2}:\\d{2}.\\d{3}", word)) {
            throw new BaseException(word + "时间格式不正确");
        }
        return "[" + StringUtils.leftPad(String.valueOf(Integer.parseInt(word.substring(1, 2)) * 60 + Integer.parseInt(word.substring(3, 5))), 2, '0') + word.substring(5, 11) + "]";
    }
}