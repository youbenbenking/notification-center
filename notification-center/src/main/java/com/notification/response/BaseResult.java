package com.notification.response;

import java.io.Serializable;

public class BaseResult implements Serializable {

	private static final long serialVersionUID = -121894632229176784L;

	private boolean success;
	private int code;
	private String message;
	
	
	public BaseResult() {
		this.success = true;
		this.code = 200;
		this.message = "成功";
	}
	
	public BaseResult(BaseResult result) {
		this.success = result.success;
		this.code = result.code;
		this.message = result.message ;
	}
	
	// 成功返回数据时调用
	public static BaseResult success() {
		return new BaseResult();
	}
	
	public static BaseResult fail(String message) {
		BaseResult result = new BaseResult();
		result.success = false;
		result.message = message;
		return result;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "BaseResult [success=" + success + ", code=" + code + ", message=" + message + "]";
	};
	
	
	
	
}
