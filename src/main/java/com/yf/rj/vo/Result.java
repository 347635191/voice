package com.yf.rj.vo;

import lombok.Data;

import java.util.List;

@Data
public class Result {
    private String code;
    private Integer size;
    private Object data;

    public static Result success(Object data) {
        Result result = new Result("0", data);
        if (data instanceof List<?>) {
            result.setSize(((List<?>) data).size());
        }
        return result;
    }

    public static Result success() {
        return new Result("0", "success");
    }

    public static Result fail(Object data) {
        return new Result("-1", data);
    }

    public Result(String code, Object data) {
        this.code = code;
        this.data = data;
    }
}