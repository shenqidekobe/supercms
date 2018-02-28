package com.dw.suppercms.infrastructure.web.websocket.listener.event;

import lombok.Getter;

import org.springframework.context.ApplicationEvent;

/**
 * 批量更新生成文件事件
 * */
public class BatchMakeFileEvent extends ApplicationEvent{

	private static final long serialVersionUID = 640102138430395621L;
	
	@Getter
	private BatchTask batchTask;
	
	public BatchMakeFileEvent(BatchTask batchTask,Object source) {
		super(source);
		this.batchTask=batchTask;
	}


}
