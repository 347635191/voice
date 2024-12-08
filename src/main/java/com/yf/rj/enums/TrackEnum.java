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
public enum TrackEnum {
    CHECK_SERIES("0"),//检查系列实时性
    CHECK_DL_TREE("1"),//检查DL tree.xlsx里未整理的
    DOWNLOAD_PIC("2"),//从DL SITE下载图片
    ;

    private String code;
    private static final Map<String, TrackEnum> values = Arrays.stream(TrackEnum.values())
            .collect(Collectors.toMap(TrackEnum::getCode, Function.identity()));

    public static TrackEnum fromCode(String code) {
        return values.get(code);
    }
}