package com.dw.suppercms.infrastructure.utils;

import java.io.Serializable;

/**
 * 用户身份信息
 * @author kobe
 * @date  2015-9-29
 * */
public class Principal implements Serializable{

	private static final long serialVersionUID = 8658089314210653905L;
	
	private Long userId;
	
	private String loginName;
	
	private String userName;
	

	public Principal(Long userId, String loginName, String userName) {
		this.userId = userId;
		this.loginName = loginName;
		this.userName = userName;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String toString() {
		return userName;
	}
	
}
