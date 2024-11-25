package com.yf.rj.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class CheckReq {
    /**
     * CheckEnum.code
     */
    @NotBlank
    @Pattern(regexp = "[0123456]")
    private String code;
}