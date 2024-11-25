package com.yf.rj.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum DailyEnum {
    ADD_TO_CHINESE_LRC("0"),//添加待汉化字幕
    ADD_ONE_LINE_LRC("1"),//添加单字幕
    VTT_TO_LRC("2"),//vtt转lrc
    TRA_TO_SIMP("3"),//繁体转简体
    FORMAT_LRC("4"),//格式化lrc
    GET_COMMENT("5"),//获取标签
    GET_ARTIST("6"),//获取艺术家
    REPLACE_KEYWORD("7"),//替换关键字
    DISCARD_SYMBOL("8"),//去符号
    ;

    private String code;
    private static final Map<String, DailyEnum> values = Arrays.stream(DailyEnum.values())
            .collect(Collectors.toMap(DailyEnum::getCode, Function.identity()));

    public static DailyEnum fromCode(String code) {
        return values.get(code);
    }
}