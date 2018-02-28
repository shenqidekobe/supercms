package com.dw.suppercms.infrastructure.utils;

import java.io.Serializable;

/**
 * system log config
 * @version 1.0
 * */
public class LogConfig implements Serializable{

	
	private static final long serialVersionUID = 6435527848328452698L;

	public enum LOG_URL_STYLE{
		rest,http
	}
	
	private String operation; //操作名称
	
	private String urlStyle; //请求风格
	
	private String urlMethod;//请求方法(rest)
	
	private String urlPattern;//请求路径

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getUrlStyle() {
		return urlStyle;
	}

	public void setUrlStyle(String urlStyle) {
		this.urlStyle = urlStyle;
	}

	public String getUrlMethod() {
		return urlMethod;
	}

	public void setUrlMethod(String urlMethod) {
		this.urlMethod = urlMethod;
	}

	public String getUrlPattern() {
		return urlPattern;
	}

	public void setUrlPattern(String urlPattern) {
		this.urlPattern = urlPattern;
	}
	
}
