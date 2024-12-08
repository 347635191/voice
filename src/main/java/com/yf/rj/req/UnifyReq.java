package com.yf.rj.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class UnifyReq {
    /**
     * UnifyEnum.code
     */
    @NotBlank
    @Pattern(regexp = "[0123]")
    private String code;

    /**
     * 旧值
     */
    private String oldWord;

    /**
     * 新值
     */
    private String newWord;

    /**
     * 替换类型
     * 1-替换标题 2-替换字幕 3-替换标题和字幕
     */
    private Integer replaceType;
}