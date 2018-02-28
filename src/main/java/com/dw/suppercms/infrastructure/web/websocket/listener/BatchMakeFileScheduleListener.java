package com.dw.suppercms.infrastructure.web.websocket.listener;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import lombok.Getter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.dw.suppercms.application.modules.ProduceFileService;
import com.dw.suppercms.application.system.ProduceLogService;
import com.dw.suppercms.domain.security.User;
import com.dw.suppercms.domain.system.ProduceLogInfo;
import com.dw.suppercms.infrastructure.web.websocket.MakeFileWebSocketHandler.MakeType;
import com.dw.suppercms.infrastructure.web.websocket.dto.MakeProcess;
import com.dw.suppercms.infrastructure.web.websocket.listener.event.BatchMakeFileEvent;
import com.dw.suppercms.infrastructure.web.websocket.listener.event.BatchTask;
import com.dw.suppercms.produce.MakeFileService;

import flexjson.JSONSerializer;

/**
 * 批量生成调度处理
 * */
@Service
public abstract class BatchMakeFileScheduleListener implements SmartApplicationListener{
	
	protected Logger log=LoggerFactory.getLogger(getClass());
	
	@Resource
	protected ProduceFileService produceFileService;
	
	@Resource
	private ProduceLogService produceLogService;
	
	@Resource
	protected MakeFileService makeFileService;
	
	@Getter
	private WebSocketSession session;
	
	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		BatchMakeFileEvent targetEvent=(BatchMakeFileEvent) event;
		BatchTask task=targetEvent.getBatchTask();
		Assert.notNull(task);
		if(supportsMakeType(task.getMakeType())){
			log.debug("开始执行websocket发布的任务："+task.getMakeType());
			this.session=task.getSession();
			try {
				execute(task);
			} catch (Exception e) {
				MakeProcess process = new MakeProcess();
				String title = String.format("批量更新出现异常:"+e.getMessage());
				process.setWarn(title);
				process.setPercent(0);
				sendMesage(new JSONSerializer().serialize(process));
				e.printStackTrace();
			}
		}
	}

	protected abstract void execute(BatchTask task);
	
	/**
	 * 发送消息
	 * */
	protected void sendMesage(String message){
		try {
			//log.debug("websocket received message:"+message);
			TextMessage textMessage=new TextMessage(message.getBytes());
			getSession().sendMessage(textMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 保存生成日志
	 * */
	protected void saveProduceLog(String produceType,String detail,List<MakeProcess> errorProcessList,int makeCount,int successCount,int failCount,String produceResult){
		String resultMsg="";
		for (int i = 0; i < errorProcessList.size(); i++) {
			MakeProcess makeProcess = errorProcessList.get(i);
			resultMsg += "<br/>" + (i + 1) + ": " + makeProcess.getTitle() + "<br/>" + makeProcess.getError();
		}
		User user=(User) this.session.getAttributes().get("user");
		ProduceLogInfo produceLog=new ProduceLogInfo();
		produceLog.setUserId(user.getId());
		produceLog.setProduceType(produceType);
		produceLog.setProduceResult(produceResult);
		produceLog.setProduceDesc(detail);
		produceLog.setProduceDate(new Date());
		produceLog.setProduceCount(makeCount);
		produceLog.setProduceSuccessCount(successCount);
		produceLog.setProduceFailCount(failCount);
		produceLog.setProduceResultMsg(resultMsg);
		produceLogService.createProduceLog(produceLog);
	}
	
	@Override
	public int getOrder(){ return 99999; };

	@Override
	public boolean supportsEventType(
			Class<? extends ApplicationEvent> eventType) {
		return eventType==BatchMakeFileEvent.class;
	}

	@Override
	public boolean supportsSourceType(Class<?> sourceType) {
		return true;
	}
	
	public abstract boolean supportsMakeType(MakeType makeType);

}
