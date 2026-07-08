package com.sagiri.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<?>> ex(Exception e){
        log.error("出现未知错误", e);
        Result<?> result = Result.error(500, "出现未知错误！");
        return ResponseEntity.status(result.getCode()).body(result);
    }
}
