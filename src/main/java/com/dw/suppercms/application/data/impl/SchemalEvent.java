package com.dw.suppercms.application.data.impl;

import java.util.Map;

import lombok.Getter;

import org.springframework.context.ApplicationEvent;

/**
 * 
 * SchemalEvent
 *
 * @author osmos
 * @date 2015年7月31日
 */
public class SchemalEvent
		extends ApplicationEvent {
	private static final long serialVersionUID = 7242273287152147647L;

	public enum SchemalEventType {
		CREATE_TABLE, RENAME_TABLE, DELETE_TABLE, CREATE_FIELD, ALTER_FIELD, DELETE_FIELD
	}

	@Getter
	private SchemalEventType eventType;
	@Getter
	private Object data = null;

	public SchemalEvent(Object source, SchemalEventType eventType, Map<String, Object> data) {
		super(source);
		this.eventType = eventType;
		this.data = data;
	}

}
