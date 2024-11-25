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
public enum CheckEnum {
    MP3_ATTR("0"),//检查音频属性
    SERIES("1"),//检查系列
    PICTURE("2"),//检查图片
    RJ("3"),//检查重复RJ和封面对应
    MP3("4"),//检查mp3
    DIR("5"),//检查文件夹
    LRC("6"),//检查lrc
    ;

    private String code;
    private static final Map<String, CheckEnum> values = Arrays.stream(CheckEnum.values())
            .collect(Collectors.toMap(CheckEnum::getCode, Function.identity()));

    public static CheckEnum fromCode(String code) {
        return values.get(code);
    }
}