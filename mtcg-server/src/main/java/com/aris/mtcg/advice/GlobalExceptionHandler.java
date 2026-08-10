package com.aris.mtcg.advice;

import com.aris.mtcg.common.exception.BusinessException;
import com.aris.mtcg.common.result.ErrorCode;
import com.aris.mtcg.common.result.Result;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 *
 * <p>Web 层不向上抛异常，统一转换为错误码响应。
 *
 * @author pengYuJun
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常
     *
     * @param e 业务异常
     * @return 统一失败响应
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Object> handleBusinessException(BusinessException e) {
        log.warn(
                "businessException code={}, message={}",
                e.getErrorCode().getCode(),
                e.getMessage());
        return Result.fail(e.getErrorCode(), e.getMessage());
    }

    /**
     * 参数校验异常（@RequestBody）
     *
     * @param e 校验异常
     * @return 统一失败响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message =
                e.getBindingResult().getFieldErrors().stream()
                        .findFirst()
                        .map(error -> error.getField() + ": " + error.getDefaultMessage())
                        .orElse(ErrorCode.PARAMS_ERROR.getMessage());
        log.warn("methodArgumentNotValidException: {}", message);
        return Result.fail(ErrorCode.PARAMS_ERROR, message);
    }

    /**
     * 参数绑定异常
     *
     * @param e 绑定异常
     * @return 统一失败响应
     */
    @ExceptionHandler(BindException.class)
    public Result<Object> handleBindException(BindException e) {
        String message =
                e.getBindingResult().getFieldErrors().stream()
                        .findFirst()
                        .map(error -> error.getField() + ": " + error.getDefaultMessage())
                        .orElse(ErrorCode.PARAMS_ERROR.getMessage());
        log.warn("bindException: {}", message);
        return Result.fail(ErrorCode.PARAMS_ERROR, message);
    }

    /**
     * 约束校验异常
     *
     * @param e 约束异常
     * @return 统一失败响应
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Object> handleConstraintViolationException(ConstraintViolationException e) {
        log.warn("constraintViolationException: {}", e.getMessage());
        return Result.fail(ErrorCode.PARAMS_ERROR, e.getMessage());
    }

    /**
     * 未捕获异常
     *
     * @param e 运行时异常
     * @return 统一失败响应
     */
    @ExceptionHandler(Exception.class)
    public Result<Object> handleException(Exception e) {
        // 数据库异常（MyBatis / JDBC）
        String msg = e.getMessage();
        if (msg != null
                && (msg.contains("PSQLException")
                        || msg.contains("BadSqlGrammarException")
                        || msg.contains("DuplicateKeyException")
                        || msg.contains("DataIntegrityViolationException"))) {
            log.error("databaseException: {}", msg);
            return Result.fail(ErrorCode.DB_ERROR, "数据库操作失败，请稍后重试");
        }
        log.error("systemException", e);
        return Result.fail(ErrorCode.SYSTEM_ERROR, "系统内部错误，请稍后重试");
    }
}
