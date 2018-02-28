package com.dw.suppercms.infrastructure.webservice.impl;

import java.util.List;

import javax.jws.WebService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.dw.party.search.cms.webservice.ICmsDataService;
import com.dw.suppercms.application.data.ColumnDataService;

/**
 * cms接收索引系统的反馈结果，进行更新栏目数据的索引状态
 * */
@WebService(endpointInterface = "com.dw.party.search.cms.webservice.ICmsDataService")
public class CmsDataServiceImpl implements ICmsDataService {

	private static final Logger log = Logger.getLogger(CmsDataServiceImpl.class);

	@Autowired
	private ColumnDataService columnDataService;

	@Override
	public void commitIndexedArticles(List<String> ids) {
		log.info("接收已索引的数据" + ids.size() + "条");
		columnDataService.modifyColumnData(ids);
		log.info("更新索引状态结束");
	}

}
