package com.yf.rj.enums;

import com.yf.rj.handler.SearchLrcHandler;
import com.yf.rj.req.SearchReq;
import com.yf.rj.vo.SearchVo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum LrcIndexEnum {
    ARTIST("a", SearchLrcHandler::keyWord);

    private String code;
    private static final Map<String, LrcIndexEnum> values = Arrays.stream(LrcIndexEnum.values())
            .collect(Collectors.toMap(LrcIndexEnum::getCode, Function.identity()));
    private BiFunction<SearchLrcHandler, SearchReq, List<SearchVo>> search;

    public static LrcIndexEnum fromCode(String code) {
        return values.get(code);
    }
}