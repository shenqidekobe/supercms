package com.dw.suppercms.application.system.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;

import com.dw.framework.core.annotation.ApplicationService;
import com.dw.suppercms.application.system.ProduceLogService;
import com.dw.suppercms.domain.system.ProduceLogInfo;
import com.dw.suppercms.domain.system.ProduceLogRepository;
import com.google.common.collect.Lists;
import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;
import com.googlecode.genericdao.search.Sort;

/**
 * 生成日志服务接口实现
 * */
@ApplicationService
public class ProduceLogServiceImpl implements ProduceLogService{
	
	@Resource
	private ProduceLogRepository produceLogRepository;

	@Override
	public boolean createProduceLog(ProduceLogInfo produceLog) {
		return this.produceLogRepository.save(produceLog);
	}

	@Override
	public ProduceLogInfo findProduceLog(Long id) {
		return this.produceLogRepository.find(id);
	}

	@Override
	public SearchResult<ProduceLogInfo> findProduceLogList(Long userId,String produceType,String produceResult,String keys,Date startTime,Date endTime,int startIndex,int maxResults){
		Search search=new Search(ProduceLogInfo.class);
		if(userId!=null){
			search.addFilterEqual("userId", userId);
		}
		if(StringUtils.isNotEmpty(produceType)){
			search.addFilterEqual("produceType",produceType);
		}
		if(StringUtils.isNotEmpty(produceResult)){
			search.addFilterEqual("produceResult", produceResult);
		}
		if(StringUtils.isNotEmpty(keys)){
			search.addFilterOr(Filter.like("produceResultMsg",  "%" +  keys+ "%"),Filter.like("produceDesc",  "%" +  keys+ "%"));
		}
		if (startTime!=null) {
			search.addFilterGreaterThan("produceDate", startTime);
		}
		if (endTime != null) {
			search.addFilterLessThan("produceDate", endTime);
		}
		search.setFirstResult(startIndex);
		search.setMaxResults(maxResults);
		search.setSorts(Lists.newArrayList(new Sort("produceDate", true)));
		return this.produceLogRepository.searchAndCount(search);
	}
}
