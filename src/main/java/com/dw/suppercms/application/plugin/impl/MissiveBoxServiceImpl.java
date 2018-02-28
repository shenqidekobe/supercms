package com.dw.suppercms.application.plugin.impl;

import javax.annotation.Resource;

import com.dw.framework.core.annotation.ApplicationService;
import com.dw.suppercms.application.plugin.MissiveBoxService;
import com.dw.suppercms.domain.plugin.MissiveBoxInfo;
import com.dw.suppercms.domain.plugin.MissiveBoxRepository;

@ApplicationService
public class MissiveBoxServiceImpl implements MissiveBoxService{
	
	@Resource
	private MissiveBoxRepository missiveBoxRepository;

	@Override
	public void saveMailBox(MissiveBoxInfo obj) {
		this.missiveBoxRepository.save(obj);
	}

}
