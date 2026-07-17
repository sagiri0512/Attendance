package com.sagiri.utils;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Web 工具类：用于直接写入 JSON 响应
 */
public class WebUtil {

    /**
     * 写入 JSON 响应（指定状态码 + Result 对象）
     *
     * @param response HttpServletResponse
     * @param status   HTTP 状态码（如 200、401、403、500）
     * @param result   响应体对象
     */
    public static void renderJson(HttpServletResponse response, int status, String result) {
        try {
            response.setStatus(status);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write(result);
        } catch (IOException e) {
            throw new RuntimeException("写入 JSON 响应失败", e);
        }
    }
}