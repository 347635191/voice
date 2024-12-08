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
    @Pattern(regexp = "[0123456789]+")
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
     * 替换类型
     * 1-替换标题 2-替换字幕 3-替换标题和字幕
     */
    private Integer replaceType;

    /**
     * 旧值
     */
    private String oldWord;

    /**
     * 新值
     */
    private String newWord;

    /**
     * 开始序号
     */
    private Integer start;

    /**
     * 结束序号
     */
    private Integer end;
}