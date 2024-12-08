package com.yf.rj.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class SearchReq {
    /**
     * Mp3IndexEnum.code
     * LrcSearchEnum.code
     */
    @NotBlank
    @Pattern(regexp = "[0123456aABC]")
    private String code;
    /**
     * 关键字
     */
    @NotBlank
    private String keyWord;
    /**
     * 返回类型 0-RJ名 1-绝对路径
     */
    @NotBlank
    @Pattern(regexp = "([01])")
    private String type;
    /**
     * 是否展示列值(SearchVo.column)
     */
    @NotNull
    private Boolean getColumn;
}