package com.yf.rj.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class TrackReq {
    /**
     * TrackEnum.code
     */
    @NotBlank
    @Pattern(regexp = "[0123]")
    private String code;
}