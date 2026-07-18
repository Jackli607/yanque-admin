package cn.edu.commons.exception;

import cn.edu.commons.apires.IErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final Integer code;

    public BusinessException(IErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(IErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    // 静态方法 方便直接抛异常
    // 如果没有静态方法 我们需要先创建当前这个自定义异常类 然后再通过 throw 进行抛出异常
    public static BusinessException of(IErrorCode errorCode) {
        throw new BusinessException(errorCode);
    }

    // 指定具体的错误信息
    public static BusinessException of(IErrorCode errorCode, String message) {
        throw new BusinessException(errorCode, message);
    }
}