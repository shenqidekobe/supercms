package com.dw.suppercms.produce.execption;

/**
 * 生成历史列表页面异常
 * */
public class MakeHistoryListExecption extends RuntimeException{
	
	private static final long serialVersionUID = 5762247485710598486L;

	public MakeHistoryListExecption(String msg){
		super(msg);
	}

}
