package com.dw.suppercms.produce.listener.event;

import lombok.Getter;

import org.springframework.context.ApplicationEvent;

/**
 * 生成模版文件事件定义
 * */
public class MakeTemplateFileEvent extends ApplicationEvent{
	
	private static final long serialVersionUID = 1L;
	
	@Getter
	private MakeFileTask task;

	public MakeTemplateFileEvent(Object source,MakeFileTask task) {
		super(source);
		this.task=task;
	}

}
