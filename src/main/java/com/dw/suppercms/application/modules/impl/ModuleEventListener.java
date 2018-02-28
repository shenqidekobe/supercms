package com.dw.suppercms.application.modules.impl;

import java.io.File;

import lombok.extern.log4j.Log4j2;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import com.dw.suppercms.domain.modules.Site;

@Service
@Log4j2
public class ModuleEventListener implements ApplicationListener<ModuleEvent> {

	@Override
	public void onApplicationEvent(ModuleEvent event) {

		log.info("processing module event: " + event.getEventType());

		switch (event.getEventType()) {
		case CREATE_SITE:
			createSiteEvent(event.getSource());
			break;
		case UPDATE_SITE:
			updateSiteEvent(event.getSource());
			break;
		case DELETE_SITE:
			deleteSiteEvent(event.getSource());
			break;
		default:
			break;
		}
	}

	private void createSiteEvent(Object source) {

		File newSiteDir = new File(((Site) source).getDirDiskpath());
		newSiteDir.mkdirs();

		String info = "created site dir: %s";
		log.info(String.format(info, newSiteDir.getAbsolutePath()));
	}

	private void updateSiteEvent(Object source) {

		Object[] objs = (Object[]) source;
		Site oldSite = (Site) objs[0];
		Site newSite = (Site) objs[1];

		if (!StringUtils.equals(oldSite.getDirName(), newSite.getDirName())) {
			File oldSiteDir = new File(oldSite.getDirDiskpath());
			File newSiteDir = new File(newSite.getDirDiskpath());
			oldSiteDir.renameTo(newSiteDir);
			String info = "rename site form %s to %s";
			log.info(String.format(info, oldSiteDir.getAbsolutePath(), newSiteDir.getAbsolutePath()));
		}
	}

	private void deleteSiteEvent(Object source) {
		File oldSiteDir = new File(((Site) source).getDirDiskpath());
		oldSiteDir.delete();
	}
}
