package com.dw.suppercms.produce.execption;

/**
 * 生成首页异常
 * */
public class MakeIndexExecption extends RuntimeException{
	
	private static final long serialVersionUID = -5425848404324761057L;

	public MakeIndexExecption(String msg){
		super(msg);
	}

}
