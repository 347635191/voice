package com.yf.rj.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class DailyReq {
    /**
     * DailyEnum.code
     */
    @NotBlank
    @Pattern(regexp = "[012345678]")
    private String code;

    /**
     * 跳过的行数
     */
    private Integer skip;

    /**
     * 是否需要转简体
     */
    private Boolean needSimplify;

    /**
     * 旧值
     */
    private String oldWord;

    /**
     * 新值
     */
    private String newWord;
}