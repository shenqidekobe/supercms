package com.dw.suppercms.produce.execption;

/**
 * 生成自定义文件异常
 * */
public class MakeCustomExecption extends RuntimeException{
	
	private static final long serialVersionUID = -625635383902401704L;

	public MakeCustomExecption(String msg){
		super(msg);
	}

}
