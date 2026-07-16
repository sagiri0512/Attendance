package com.sagiri.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 参数校验失败（@Valid 校验不通过）
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<?>> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.status(400).body(Result.error(400, msg));
    }

    // 参数类型转换失败（如传字符串给 Integer 参数）
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Result<?>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        String msg = "参数 " + e.getName() + " 类型错误";
        return ResponseEntity.status(400).body(Result.error(400, msg));
    }

    // 缺少请求参数
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Result<?>> handleMissingParam(MissingServletRequestParameterException e) {
        String msg = "缺少必要参数: " + e.getParameterName();
        return ResponseEntity.status(400).body(Result.error(400, msg));
    }

    // 请求方法不支持（如该用 POST 却用了 GET）
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Result<?>> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity.status(405).body(Result.error(405, "请求方法不支持"));
    }

    //工号或密码错误
    @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
    public ResponseEntity<Result<?>> userNameNotFound(){
        return ResponseEntity.status(401).body(Result.error(401, "工号或密码错误！"));
    }

    // 兜底：未知异常
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<?>> handleException(Exception e) {
        log.error("出现未知错误", e);
        return ResponseEntity.status(500).body(Result.error(500, "服务器内部错误"));
    }
}

