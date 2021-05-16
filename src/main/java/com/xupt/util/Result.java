package com.xupt.util;

import com.xupt.enums.Code;
import lombok.Data;

/**
 * @Auther: yhn
 * @Date: 2021/3/6 16:31
 */
@Data
public class Result<T> {

    private T data;

    private String message;

    private int code;

    private Result(T data, String message, int code) {
        this.data = data;
        this.message = message;
        this.code = code;
    }

    public static Result<Object> success(Object data) {
        return new Result<>(data, "成功", Code.SUCCESS.getCode());
    }

    public static Result<Object> error(String message, int code) {
        return new Result<>(null, message, code);
    }
}
