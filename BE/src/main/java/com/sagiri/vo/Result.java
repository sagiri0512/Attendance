package com.sagiri.vo;

import lombok.Data;

/**
 * 统一响应结果封装
 *
 * <p>所有Controller接口统一使用此泛型类封装响应，
 * 包含状态码、消息和泛型数据体。
 * 提供静态工厂方法快速构建成功/错误响应。</p>
 *
 * @param <T> 响应数据类型
 * @author sagiri
 */
@Data
public class Result<T> {

    /** 状态码（200-成功，其他-错误） */
    private int code;
    /** 提示消息 */
    private String message;
    /** 响应数据 */
    private T data;

    /**
     * 构建成功响应
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return code=200的Result
     */
    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.code = 200;
        r.data = data;
        return r;
    }

    /**
     * 构建错误响应
     *
     * @param code    错误状态码
     * @param message 错误提示消息
     * @param <T>     数据类型
     * @return 错误Result
     */
    public static <T> Result<T> error(int code, String message) {
        Result<T> r = new Result<>();
        r.code = code;
        r.message = message;
        return r;
    }
}

