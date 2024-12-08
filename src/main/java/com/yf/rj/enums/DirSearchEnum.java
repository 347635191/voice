package com.yf.rj.enums;

import com.yf.rj.handler.SearchDirHandler;
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
public enum DirSearchEnum {
    CREATE_TIME("A", SearchDirHandler::createTime),//查询指定日期后整理的音声
    SEARCH_DIR("B", SearchDirHandler::searchDir),//RJ文件夹名查询关键字
    SEARCH_FILE("C", SearchDirHandler::searchFile);//文件名搜索关键字

    private String code;
    private static final Map<String, DirSearchEnum> values = Arrays.stream(DirSearchEnum.values())
            .collect(Collectors.toMap(DirSearchEnum::getCode, Function.identity()));
    private BiFunction<SearchDirHandler, SearchReq, List<SearchVo>> search;

    public static DirSearchEnum fromCode(String code) {
        return values.get(code);
    }
}