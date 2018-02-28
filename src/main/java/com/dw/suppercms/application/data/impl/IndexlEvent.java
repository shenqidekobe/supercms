package com.dw.suppercms.application.data.impl;

import java.util.Map;

import lombok.Getter;

import org.springframework.context.ApplicationEvent;

/**
 * 
 * IndexlEvent
 *
 * @author osmos
 * @date 2015年8月13日
 */
public class IndexlEvent
		extends ApplicationEvent {
	private static final long serialVersionUID = 5446229927956775771L;

	public enum IndexlEventEventType {
		UPDATE_INDEX, DELETE_INDEX
	}

	@Getter
	private IndexlEventEventType eventType;
	@Getter
	private Object data = null;

	public IndexlEvent(Object source, IndexlEventEventType eventType, Map<String, Object> data) {
		super(source);
		this.eventType = eventType;
		this.data = data;
	}

}
