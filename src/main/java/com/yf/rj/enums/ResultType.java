package com.yf.rj.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ResultType {
    MAME("0", "RJ名"),
    PATH("1", "绝对路径");

    private String code;
    private String desc;
}