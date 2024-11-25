package com.yf.rj.config;

import com.yf.rj.dto.BaseException;
import com.yf.rj.vo.Result;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@ControllerAdvice
public class SpringControllerAdvice {
    private static final Logger LOG = LoggerFactory.getLogger(SpringControllerAdvice.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Result validationExceptionHandler(MethodArgumentNotValidException e) {
        String message = Optional.ofNullable(e)
                .map(BindException::getFieldError)
                .map(err -> err.getField() + err.getDefaultMessage())
                .orElse(StringUtils.EMPTY);
        return Result.fail(message);
    }

    @ExceptionHandler(BindException.class)
    @ResponseBody
    public Result bindExceptionHandler(BindException e) {
        String message = Optional.ofNullable(e)
                .map(BindException::getFieldError)
                .map(err -> err.getField() + err.getDefaultMessage())
                .orElse(StringUtils.EMPTY);
        return Result.fail(message);
    }

    @ExceptionHandler
    @ResponseBody
    public Result exceptionHandler(Throwable e) {
        LOG.warn("内部异常", e);
        return Result.fail("服务器内部错误");
    }

    @ExceptionHandler(BaseException.class)
    @ResponseBody
    public Result baseExceptionHandler(BaseException e) {
        return Result.fail(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public Result runtimeExceptionHandler(RuntimeException e) {
        LOG.error("运行时异常：{}", String.valueOf(e));
        return Result.fail(e.getMessage());
    }
}