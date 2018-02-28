package com.dw.suppercms.infrastructure.web.websocket.dto;

import lombok.Data;

import com.dw.suppercms.infrastructure.web.websocket.MakeFileWebSocketHandler.MakeType;

@Data
public class MakeProcess {

	private Object obj;
	private MakeType type;
	private String title;
	private int percent;
	private String error;
	private String warn;
	private boolean isComplete=false;

}
