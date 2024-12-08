package com.yf.rj.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum ReplaceTypeEnum {
    TITLE(1, "替换标题"),
    CONTENT(2, "替换字幕"),
    ALL(3, "替换标题和字幕");

    private Integer code;
    private String desc;
    private static final List<ReplaceTypeEnum> values = Arrays.asList(values());

    public static ReplaceTypeEnum fromCode(Integer code) {
        return values.stream().filter(ele -> ele.getCode().equals(code)).findFirst().orElse(null);
    }
}