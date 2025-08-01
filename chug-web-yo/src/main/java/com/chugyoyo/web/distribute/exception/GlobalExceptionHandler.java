package com.chugyoyo.web.distribute.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.lang.reflect.UndeclaredThrowableException;

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

    /**
     * UndeclaredThrowableException 本质：
     * <p>
     * 当代理对象（Spring AOP 生成的）抛出了被代理方法未声明的“检查异常”（非 RuntimeException 的异常）时发生，原始方法未声明 throws xxException
     *
     * @param e UndeclaredThrowableException
     * @return
     */
    @ExceptionHandler(UndeclaredThrowableException.class)
    public ResponseEntity<String> handleUndeclaredThrowableException(Exception e) {
        log.error(e.getMessage(), e);
        return errorResponse("系统异常，请联系管理员", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        log.error(e.getMessage(), e);
        return errorResponse("系统异常，请联系管理员", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 这里返回的格式，与前端约定好，ResponseEntity：http 层面，body 可以封装自定义的格式
    public static ResponseEntity<String> errorResponse(String message, HttpStatus status) {
        return ResponseEntity.status(status).body(message);
    }

}
