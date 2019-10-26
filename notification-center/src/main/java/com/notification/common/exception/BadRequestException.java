package com.notification.common.exception;

import java.text.MessageFormat;
import java.util.function.Supplier;

import org.springframework.http.HttpStatus;
import com.notification.common.enums.ResultCode;

public class BadRequestException extends ApiException {
    private static final long serialVersionUID = -2370931178164898699L;

    public BadRequestException(HttpStatus status, ResultCode resultCode, String message) {
        super(status, resultCode, message);
    }

    public BadRequestException(ResultCode resultCode) {
        super(HttpStatus.BAD_REQUEST, resultCode, resultCode.getMessage());
    }

    public BadRequestException(ResultCode resultCode, Object... args) {
        super(HttpStatus.BAD_REQUEST, resultCode, MessageFormat.format(resultCode.getMessage(), args));
    }

    public static Supplier<BadRequestException> of(ResultCode resultCode, Object... args) {
        return () -> new BadRequestException(resultCode, args);
    }

}
