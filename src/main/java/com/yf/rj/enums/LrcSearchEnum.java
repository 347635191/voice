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
public enum LrcSearchEnum {
    KEY_WORD("a", SearchLrcHandler::keyWord);

    private String code;
    private static final Map<String, LrcSearchEnum> values = Arrays.stream(LrcSearchEnum.values())
            .collect(Collectors.toMap(LrcSearchEnum::getCode, Function.identity()));
    private BiFunction<SearchLrcHandler, SearchReq, List<SearchVo>> search;

    public static LrcSearchEnum fromCode(String code) {
        return values.get(code);
    }
}