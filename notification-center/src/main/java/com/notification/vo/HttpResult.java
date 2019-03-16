/**
 * Copyright(c) 2013-2017 by Puhuifinance Inc.
 * All Rights Reserved
 */
package com.notification.vo;
/**
 * http请求结果
 * 
 * @author LYC
 */
public class HttpResult {

	/**
	 * 成功状态码
	 */
	public static final Integer SUCCESS_STATUS = 200;

    /**
     * 状态码
     */
    private Integer status;
    /**
     * 返回数据
     */
    private String data;

    public HttpResult() {
    }

    public HttpResult(Integer status, String data) {
        this.status = status;
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "{\"status\": \"" + status + "\", \"data\": \"" + data + "\"}";
    }

}