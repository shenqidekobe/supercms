package com.dw.suppercms.application.plugin.impl;

import javax.annotation.Resource;

import com.dw.framework.core.annotation.ApplicationService;
import com.dw.suppercms.application.plugin.IdeasBoxService;
import com.dw.suppercms.domain.plugin.IdeasBoxInfo;
import com.dw.suppercms.domain.plugin.IdeasBoxRepository;

@ApplicationService
public class IdeasBoxServiceImpl  implements IdeasBoxService{
	
	@Resource
	private IdeasBoxRepository ideasBoxRepository;

	@Override
	public void saveIdeasBox(IdeasBoxInfo obj) {
		this.ideasBoxRepository.save(obj);
	}

}
