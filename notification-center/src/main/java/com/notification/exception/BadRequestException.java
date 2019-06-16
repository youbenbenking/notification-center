package com.notification.exception;

import java.text.MessageFormat;

import org.springframework.http.HttpStatus;

import com.notification.enums.ResultCode;
/**
 * 统一异常抛出，减少代码冗余
 * @author xuyun
 *
 */
public class BadRequestException extends ApiException{

	private static final long serialVersionUID = -2370931178164898699L;
	
	public BadRequestException( HttpStatus httpStatus, ResultCode resultCode, String message) {
		super(httpStatus, resultCode, message);
	}
	
	public BadRequestException(ResultCode resultCode) {
		super(HttpStatus.BAD_REQUEST, resultCode, resultCode.getMessage());
	}
	
	public BadRequestException(ResultCode resultCode, Object... args) {
		super(HttpStatus.BAD_REQUEST, resultCode, MessageFormat.format(resultCode.getMessage(), args) );
	}
}
