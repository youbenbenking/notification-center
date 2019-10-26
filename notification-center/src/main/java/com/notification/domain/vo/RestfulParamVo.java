package com.notification.domain.vo;

/**
 * 请求参数
 * 
 * @author youb
 * @date 2018年11月10日
 * @param <T>
 */
public class RestfulParamVo<T> {

	/**
	 * 请求密码
	 */
	private String token;

	/**
	 * 请求用户名
	 */
    private String source;

	/**
	 * 请求唯一编号（需要调用方确保该字段唯一）
	 */
	private String requestSeq;

	/**
	 * 请求参数详情
	 */
    private T param;

	public RestfulParamVo() {
		super();
	}

	public RestfulParamVo(String source, String token) {
		super();
		this.token = token;
		this.source = source;
	}

	public RestfulParamVo(String token, String source, T param) {
		super();
		this.token = token;
		this.source = source;
		this.param = param;
	}

	public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public T getParam() {
        return param;
    }

    public void setParam(T param) {
        this.param = param;
    }

	public String getRequestSeq() {
		return requestSeq;
	}

	public void setRequestSeq(String requestSeq) {
		this.requestSeq = requestSeq;
	}

	@Override
	public String toString() {
		return "{token:" + token + ", source:" + source + ", requestSeq:" + requestSeq + ", param:" + param + "}";
	}
    
}

