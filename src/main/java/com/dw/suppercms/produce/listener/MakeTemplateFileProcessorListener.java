package com.dw.suppercms.produce.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.dw.suppercms.produce.listener.event.MakeFileTask;
import com.dw.suppercms.produce.listener.event.MakeFileTask.MakeEventType;
import com.dw.suppercms.produce.listener.event.MakeTemplateFileEvent;

/**
 * 生成模版文件监听器
 * */
@Service
public abstract class MakeTemplateFileProcessorListener implements SmartApplicationListener{
	
	protected Logger logger=LoggerFactory.getLogger(getClass());
	
	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		MakeTemplateFileEvent targetEvent=(MakeTemplateFileEvent) event;
		MakeFileTask task=targetEvent.getTask();
		Assert.notNull(task);
		if(supportsMakeType(task.getMakeEventType())){
			makeFile(task);
		}
	}

	protected abstract void makeFile(MakeFileTask task);
	
	@Override
	public int getOrder(){ return 0; };

	@Override
	public boolean supportsEventType(
			Class<? extends ApplicationEvent> eventType) {
		return eventType==MakeTemplateFileEvent.class;
	}

	@Override
	public boolean supportsSourceType(Class<?> sourceType) {
		return true;
	}
	
	public abstract boolean supportsMakeType(MakeEventType makeEventType);
	
}
