package cn.edu.commons.apires;

import lombok.Getter;

@Getter
public enum CommonErrorCode implements IErrorCode{
    //成功
    SUCCESS(200, "成功"),
    FAIL(500, "失败"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止"),
    NOT_FOUND(404, "不存在"),
    INTERNAL_SERVER_ERROR(500, "服务器错误"),
    PASSWORD_ERROR(400, "用户名或者密码错误"),
    ACCOUNT_NOT_ACTIVE(405, "账号未启用");

    private final Integer code;
    private final String msg;

    CommonErrorCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.msg;
    }
}
