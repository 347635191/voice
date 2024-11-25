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
}