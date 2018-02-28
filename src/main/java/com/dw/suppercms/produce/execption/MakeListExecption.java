package com.dw.suppercms.produce.execption;

/**
 * 生成列表页面异常
 * */
public class MakeListExecption extends RuntimeException{
	
	private static final long serialVersionUID = 5762247485710598486L;

	public MakeListExecption(String msg){
		super(msg);
	}

}
