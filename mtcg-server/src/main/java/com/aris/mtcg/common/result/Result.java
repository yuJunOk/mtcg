package com.aris.mtcg.common.result;

import com.aris.mtcg.domain.vo.PageVO;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 统一响应结果封装
 *
 * <p>命名使用 Result 而非 ResponseEntity，避免与 Spring 同名类冲突。
 *
 * @param <T> 业务数据类型
 * @author pengYuJun
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Result<T> implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    /** 状态码：0 表示成功，其他表示失败 */
    private int code;

    /** 响应数据 */
    private T data;

    /** 提示信息 */
    private String message;

    public Result(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }

    public Result(ErrorCode errorCode, T data) {
        this(errorCode.getCode(), data, errorCode.getMessage());
    }

    public Result(ErrorCode errorCode, T data, String message) {
        this(errorCode.getCode(), data, message);
    }

    public static <T> Result<T> success() {
        return new Result<>(ErrorCode.SUCCESS);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(ErrorCode.SUCCESS, data);
    }

    public static <T> Result<T> success(T data, String message) {
        return new Result<>(ErrorCode.SUCCESS.getCode(), data, message);
    }

    public static <T> Result<T> fail(ErrorCode errorCode) {
        return new Result<>(errorCode);
    }

    public static <T> Result<T> fail(ErrorCode errorCode, String message) {
        return new Result<>(errorCode, null, message);
    }

    public static <T> Result<PageVO<T>> page(List<T> records, long total) {
        PageVO<T> data = new PageVO<>(records, total);
        return new Result<>(ErrorCode.SUCCESS, data);
    }

    /**
     * 是否成功
     *
     * @return true 表示业务成功
     */
    public boolean isSuccess() {
        return this.code == ErrorCode.SUCCESS.getCode();
    }
}
