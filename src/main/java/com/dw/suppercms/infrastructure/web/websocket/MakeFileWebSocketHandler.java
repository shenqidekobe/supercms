package com.dw.suppercms.infrastructure.web.websocket;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.dw.suppercms.infrastructure.web.websocket.listener.event.BatchMakeFileEvent;
import com.dw.suppercms.infrastructure.web.websocket.listener.event.BatchTask;

/**
 * 生成文件处理websocket 继承自springWebsocket的文本消息处理器
 * 
 * TextWebSocketHandler extends AbstractWebSocketHandler implemens WebSocketHandler
 * 
 * @author kobe
 * @version 1.0
 * */
public class MakeFileWebSocketHandler extends TextWebSocketHandler {

	private Logger logger = org.slf4j.LoggerFactory.getLogger(MakeFileWebSocketHandler.class);
	
	@Resource
	private ApplicationContext applicationContext;
	
	public enum MakeType {
		CUSTOM, INDEX, LIST_CONTENT_INCREMENT, SELF_INCREMENT, CONTENT, CONTENT_ALONE,LIST,TEST;
	}
	
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String clientMsg=message.getPayload();
		if (StringUtils.isEmpty(clientMsg)) {
			logger.info("websocket message is null or empty");
		} else {
			logger.info("接收 Websocket Message: " + clientMsg);
			String[] split = clientMsg.split("#");
			MakeType makeType = MakeType.valueOf(split[0]);
			try {
				String params=getRequestParams(makeType, clientMsg, split);
				BatchTask task=new BatchTask();
				task.setSession(session);
				task.setMakeType(makeType);
				task.setParams(params);
				applicationContext.publishEvent(new BatchMakeFileEvent(task,this));
			} catch( Throwable e){
				e.printStackTrace();
			}
		}
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session)
			throws Exception {
		logger.debug("connect to the websocket success...localAddress="+session.getLocalAddress());
	}

	@Override
	public void handleTransportError(WebSocketSession session,
			Throwable exception) throws Exception {
		if (session.isOpen()) {
			session.close();
		}
		logger.debug("websocket connection closed...exeception="+exception.getMessage());
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session,
			CloseStatus closeStatus) throws Exception {
		logger.debug("websocket connection closed...closeStatus.code="+closeStatus.getCode());
	}

	@Override
	public boolean supportsPartialMessages() {
		return false;
	}
	
	
	private String getRequestParams(MakeType type,String clientMsg,String[] split){
		String params="";
		switch (type) {
		case SELF_INCREMENT:
			params=split[1];
			break;
		case CONTENT:
			params=clientMsg;
			break;
		case CONTENT_ALONE:
			params=clientMsg;
			break;
		case LIST:
			params=clientMsg;
			break;
		case CUSTOM:
			params=split[1];
			break;
		case INDEX:
			params=split[1];
			break;
		case LIST_CONTENT_INCREMENT:
			params=split[1];
		case TEST:
			params="63";
		default:
			break;
		}
		return params;
	}

}
