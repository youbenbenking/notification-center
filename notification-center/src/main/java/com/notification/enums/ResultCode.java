package com.notification.enums;

public enum ResultCode {

	SUCCESS(200, "成功"),
	SYSTEM_ERROR(-99, "系统错误"),
	DB_ERROR(-98, "数据库错误"),
	DATA_NOT_FOUND(-1, "数据不存在，{0}"),
	ILLEGAL_PARAMETER(-2, "参数不合法，{%s}={%s}")
	;
	
	private final Integer code;
	
	private final String message;
	
	
	private ResultCode(Integer code, String message) {
		this.code = code;
		this.message = message;
	}
	
	/**
	 * 调用该方法一定要注意，占位符和数组大小保持一致
	 * @param resultCode
	 * @param arr
	 * @return
	 */
	public static String getMessage(ResultCode resultCode, String... arr) {
		return String.format(resultCode.message, arr);
	}

	public Integer getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}
