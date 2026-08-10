package com.aris.mtcg.common.exception;

import com.aris.mtcg.common.result.ErrorCode;
import lombok.Getter;

/**
 * 业务异常
 *
 * <p>手册建议：禁止直接抛出 RuntimeException / Exception，应使用有业务含义的自定义异常。
 *
 * @author pengYuJun
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    private final String message;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }
}
