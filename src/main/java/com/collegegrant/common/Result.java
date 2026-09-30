package com.collegegrant.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private Integer code;   //状态码 200 成功;500 失败;
    private String msg;     //提示信息
    private T data;         //返回数据信息


    // 方法1：查询成功，需要返回数据
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    // 方法2：增删改成功，不需要返回数据
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }

    // 方法3：失败，返回错误信息
    public static <T> Result<T> error(String msg) {
        return new Result<>(500, msg, null);
    }

}
