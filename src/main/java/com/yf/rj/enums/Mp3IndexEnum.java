package com.yf.rj.enums;

import com.googlecode.cqengine.attribute.Attribute;
import com.yf.rj.cache.Mp3Db;
import com.yf.rj.entity.Mp3T;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum Mp3IndexEnum {
    ARTIST("0", Mp3Db.Index.ARTIST, Mp3T::getArtist),
    SERIES("1", Mp3Db.Index.SERIES, Mp3T::getSeries),
    COMPOSER("2", Mp3Db.Index.COMPOSER, Mp3T::getComposer),
    GENRE("3", Mp3Db.Index.GENRE, Mp3T::getGenre),
    COMMENT("4", Mp3Db.Index.COMMENT, Mp3T::getComment),
    RJ_NAME("5", Mp3Db.Index.RJ_NAME, Mp3T::getRjName),
    FILE_NAME("6", Mp3Db.Index.FILE_NAME, Mp3T::getFileName);
    private String code;
    private Attribute<Mp3T, String> index;
    private static final Map<String, Mp3IndexEnum> values = Arrays.stream(Mp3IndexEnum.values())
            .collect(Collectors.toMap(Mp3IndexEnum::getCode, Function.identity()));
    private Function<Mp3T, String> column;

    public static Mp3IndexEnum fromCode(String code) {
        return values.get(code);
    }
}