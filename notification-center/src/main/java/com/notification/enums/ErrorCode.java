package com.notification.enums;

public enum ErrorCode {

    TOKEN_VALIDATE_ERROR("TOKEN_VALIDATE_ERROR", "token验证失败."),
    MSG_ENTER_QUEUE_ERROR("MSG_ENTER_QUEUE_ERROR", "消息通知入队异常."),
    QUERY_CHANE_MSG_ERROR("QUERY_CHANE_MSG_ERROR", "获取缓存消息异常."),
    SET_CHANE_MSG_ERROR("SET_CHANE_MSG_ERROR", "设置缓存消息异常.");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
