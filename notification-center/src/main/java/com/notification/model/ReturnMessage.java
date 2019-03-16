package com.notification.model;

import java.io.Serializable;

/**
 * 错误信息
 * 
 * @author youb
 * @date 2018年7月8日
 */
public class ReturnMessage implements Serializable {
    
    private static final long serialVersionUID = -4191752785867342795L;
    /**
     * 是否成功
     */
    private boolean success;
    /**
     * 信息
     */
    private String msg;
    /**
     * 错误码
     */
    private String code;
    /**
     * 错误码
     */
    /**
     * 数据1
     */
    private Object data;
    
    /**
     * 数据2
     */
    private Object dataTwo;
    
    

	public Object getDataTwo() {
		return dataTwo;
	}

	public void setDataTwo(Object dataTwo) {
		this.dataTwo = dataTwo;
	}


	public ReturnMessage() {

    }

    public ReturnMessage(boolean success, String msg) {
        this.setSuccess(success);
        this.setMsg(msg);
    }

    public ReturnMessage(boolean success, String msg, Object data) {
        this(success, msg);
        this.setData(data);
    }

	public ReturnMessage(boolean success, String msg, Object data, String code) {
		this(success, msg, data);
	}

    public boolean success() {
        return success;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    
    

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

	@Override
	public String toString() {
		return "{success:" + success + ", msg:" + msg + ", code:" + code + ", data:" + data + ", dataTwo:" + dataTwo+ "}";
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
