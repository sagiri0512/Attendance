package com.sagiri.common;

import com.sagiri.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * <p>统一拦截Controller层抛出的各类异常，返回标准Result格式的JSON响应。
 * 覆盖7类异常场景：参数校验、类型转换、缺少参数、请求方法不支持、
 * 登录失败、权限不足、未知错误。</p>
 *
 * @author sagiri
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 参数校验失败（@Valid 校验不通过）
     *
     * <p>提取所有字段错误信息，拼接后返回。</p>
     *
     * @param e 参数校验异常
     * @return 400 + 字段错误详情
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<?>> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.status(400).body(Result.error(400, msg));
    }

    /**
     * 参数类型转换失败（如传字符串给 Integer 参数）
     *
     * @param e 类型转换异常
     * @return 400 + 参数名
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Result<?>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        String msg = "参数 " + e.getName() + " 类型错误";
        return ResponseEntity.status(400).body(Result.error(400, msg));
    }

    /**
     * 缺少必要请求参数
     *
     * @param e 缺少参数异常
     * @return 400 + 参数名
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Result<?>> handleMissingParam(MissingServletRequestParameterException e) {
        String msg = "缺少必要参数: " + e.getParameterName();
        return ResponseEntity.status(400).body(Result.error(400, msg));
    }

    /**
     * 请求方法不支持（如该用 POST 却用了 GET）
     *
     * @param e 方法不支持异常
     * @return 405
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Result<?>> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity.status(405).body(Result.error(405, "请求方法不支持"));
    }

    /**
     * 登录失败：工号不存在或密码错误
     *
     * <p>统一处理 {@link UsernameNotFoundException} 和 {@link BadCredentialsException}。</p>
     *
     * @return 401
     */
    @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
    public ResponseEntity<Result<?>> handleLoginFailure() {
        return ResponseEntity.status(401).body(Result.error(401, "工号或密码错误！"));
    }

    /**
     * @PreAuthorize 权限不足
     *
     * <p>Spring Security 6 权限校验失败抛出 {@link AuthorizationDeniedException}。</p>
     *
     * @return 403
     */
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Result<?>> handleAuthorizationDenied() {
        return ResponseEntity.status(403).body(Result.error(403, "权限不足！"));
    }

    /**
     * 兜底：未知异常
     *
     * <p>捕获所有未被上述处理器匹配的异常，记录日志并返回500。</p>
     *
     * @param e 异常对象
     * @return 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<?>> handleException(Exception e) {
        log.error("出现未知错误", e);
        return ResponseEntity.status(500).body(Result.error(500, "服务器内部错误"));
    }
}

