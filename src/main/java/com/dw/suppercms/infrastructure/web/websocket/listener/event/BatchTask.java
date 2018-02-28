package com.dw.suppercms.infrastructure.web.websocket.listener.event;

import java.io.Serializable;

import org.springframework.web.socket.WebSocketSession;

import lombok.Data;

import com.dw.suppercms.infrastructure.web.websocket.MakeFileWebSocketHandler.MakeType;

/**
 * 批量任务
 * */
@Data
public class BatchTask implements Serializable{
	
	private static final long serialVersionUID = -5687033521605344021L;
	
	private MakeType makeType;
	
	private String params;
	
	private WebSocketSession session;

}
