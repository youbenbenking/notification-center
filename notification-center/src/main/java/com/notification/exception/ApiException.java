package com.notification.exception;

import org.springframework.http.HttpStatus;
import com.notification.enums.ResultCode;

public class ApiException extends RuntimeException {
    private static final long serialVersionUID = -771492678073174883L;
    private final HttpStatus httpStatus;
    private final ResultCode resultCode;

    public ApiException(HttpStatus httpStatus, ResultCode resultCode, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.resultCode = resultCode;
    }

    public HttpStatus getHttpstatus() {
        return httpStatus;
    }

    public ResultCode getResultcode() {
        return resultCode;
    }

}
