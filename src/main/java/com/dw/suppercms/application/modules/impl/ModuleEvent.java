package com.dw.suppercms.application.modules.impl;

import lombok.Getter;

import org.springframework.context.ApplicationEvent;

/**
 * 
 * ModuleEvent
 *
 * @author osmos
 * @date 2015年6月12日
 */
public class ModuleEvent
		extends ApplicationEvent {
	private static final long serialVersionUID = -1652043436416578605L;

	@Getter
	private ModuleEventType eventType;

	public enum ModuleEventType {
		CREATE_SITE, UPDATE_SITE, DELETE_SITE,
		CREATE_COLUMN, UPDATE_COLUMN, DELETE_COLUMN,
		CREATE_CUSTOM, UPDATE_CUSTOM, DELETE_CUSTOM,
	}

	public ModuleEvent(ModuleEventType eventType, Object source) {
		super(source);
		this.eventType = eventType;
	}

}
