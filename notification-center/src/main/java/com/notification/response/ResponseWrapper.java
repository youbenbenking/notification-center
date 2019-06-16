package com.notification.response;

import java.io.Serializable;
import java.util.Objects;

import org.springframework.http.HttpStatus;

import com.notification.enums.ResultCode;
import com.notification.exception.NotSerializableException;

public class ResponseWrapper<T> implements Serializable {

	private static final long serialVersionUID = -121894632229176784L;

	private T data;
	private boolean success;
	private ResultCode resultCode;
	private String message;
	private HttpStatus httpStatus;
	
	public ResponseWrapper() {
	}
	
	// 成功返回数据时调用
	public static <T> ResponseWrapper<T> success(T data) {
		if (Objects.nonNull(data) && !(data instanceof Serializable)) {
			throw new NotSerializableException(data.getClass());
		}
		
		ResponseWrapper<T> responseWrapper = new ResponseWrapper<T>();
		responseWrapper.success = true;
		responseWrapper.message = "ok";
		responseWrapper.data = data;
		return responseWrapper;
	}
	
	// 成功不返回数据时调用
	public static <T> ResponseWrapper<T> success() {
		return success(null);
	};
	
	
	// 失败返回数据时调用
	public static <T> ResponseWrapper<T> fail(HttpStatus httpStatus, ResultCode resultCode, String message, T data) {
		ResponseWrapper<T> responseWrapper = new ResponseWrapper<T>();
		responseWrapper.success = true;
		responseWrapper.httpStatus = httpStatus;
		responseWrapper.resultCode = resultCode;
		responseWrapper.data = data;
		return responseWrapper;
	}
	
	public static <T> ResponseWrapper<T> fail(HttpStatus httpStatus, ResultCode resultCode, T data) {
		return fail(httpStatus, resultCode, resultCode.getMessage(), data);
	};
		
	public static <T> ResponseWrapper<T> fail(HttpStatus httpStatus, ResultCode resultCode, String message) {
		return fail(httpStatus, resultCode, message, null);
	};
		
	public static <T> ResponseWrapper<T> fail(ResultCode resultCode, String message) {
		return fail(null, resultCode, message);
	};
	
	public static <T> ResponseWrapper<T> fail(ResultCode resultCode) {
		return fail(resultCode, resultCode.getMessage());
	}

	public T getData() {
		return data;
	}


	public boolean isSuccess() {
		return success;
	}

	public ResultCode getResultCode() {
		return resultCode;
	}


	public String getMessage() {
		return message;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
	
}
