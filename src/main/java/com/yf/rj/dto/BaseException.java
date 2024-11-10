package com.yf.rj.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BaseException extends Exception {
    private static final long serialVersionUID = 8510328283193587887L;
    private String message;

    public static BaseException innerErr(){
        return new BaseException("系统错误");
    }
}