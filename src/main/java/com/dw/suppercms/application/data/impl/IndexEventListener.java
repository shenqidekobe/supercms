package com.dw.suppercms.application.data.impl;

import java.util.Map;

import lombok.extern.log4j.Log4j2;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

/**
 * 
 * SchemalEventListener
 *
 * @author osmos
 * @date 2015年7月31日
 */
@Service
@Log4j2
@SuppressWarnings("unchecked")
public class IndexEventListener implements ApplicationListener<IndexlEvent> {

	@Override
	public void onApplicationEvent(IndexlEvent event) {

		log.info("processing index event: " + event.getEventType());
		Map<String, Object> data = (Map<String, Object>) event.getData();
		switch (event.getEventType()) {
		case UPDATE_INDEX:
			updateIndex(data);
			break;
		case DELETE_INDEX:
			deleteIndex(data);
			break;
		default:
			break;
		}
	}

	private void updateIndex(Map<String, Object> data) {
		log.info("TODO");
	}

	private void deleteIndex(Map<String, Object> data) {
		log.info("TODO");
	}
}
