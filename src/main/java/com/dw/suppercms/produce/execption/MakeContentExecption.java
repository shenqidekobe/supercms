package com.dw.suppercms.produce.execption;

/**
 * 生成内容页异常
 * */
public class MakeContentExecption extends RuntimeException{
	
	private static final long serialVersionUID = -5939024695709973255L;

	public MakeContentExecption(String msg){
		super(msg);
	}

}
