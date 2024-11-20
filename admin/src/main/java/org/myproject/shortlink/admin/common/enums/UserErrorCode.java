package org.myproject.shortlink.admin.common.enums;

import org.myproject.shortlink.admin.common.convention.errorcode.IErrorCode;

public enum UserErrorCode implements IErrorCode {
    USER_NULL("B000200", "用户不存在"),
    USER_NAME_EXIST("B000201", "用户名已存在"),
    USER_EXIST("B000202", "用户已存在"),
    USER_SAVE_FAILURE("B000203", "用户新增失败"),
    USER_HAS_LOGIN("B000204", "用户已登录"),
    USER_NOT_LOGIN("B0002005", "用户未登录");

    private final String code;
    private final String message;

    UserErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }

}
