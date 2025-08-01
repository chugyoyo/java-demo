package com.chugyoyo.web.distribute.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 *
 * @author chugyoyo
 * @since 2025/8/1
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        log.error(e.getMessage(), e);
        // 只有 RuntimeException，e.getMessage() 才不为 null
        return errorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobalException(Exception e) {
        log.error(e.getMessage(), e);
        return errorResponse("系统异常，请联系管理员", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static ResponseEntity<String> errorResponse(String message, HttpStatus status) {
        return ResponseEntity.status(status).body(message);
    }

}
