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
public enum UnifyEnum {
    TITLE_AND_ALBUM("0"),//设置标题和唱片集
    GET_TRA("1"),//获取繁体字
    REPLACE_KEYWORD("2"),//替换关键字
    DISCARD_SYMBOL("3"),//去特殊符号
    ;

    private String code;
    private static final Map<String, UnifyEnum> values = Arrays.stream(UnifyEnum.values())
            .collect(Collectors.toMap(UnifyEnum::getCode, Function.identity()));

    public static UnifyEnum fromCode(String code) {
        return values.get(code);
    }
}