package com.dw.suppercms.application.system.impl;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.dw.framework.core.annotation.ApplicationService;
import com.dw.suppercms.application.system.SysLogService;
import com.dw.suppercms.domain.system.SysLogInfo;
import com.dw.suppercms.domain.system.SysLogInfo.OPER_TYPE;
import com.dw.suppercms.domain.system.SysLogRepository;
import com.dw.suppercms.domain.system.SysLogInfo.MODULE_LOG;
import com.google.common.collect.Lists;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;
import com.googlecode.genericdao.search.Sort;

/**
 * 分类服务
 * */
@ApplicationService
public class SysLogServiceImpl implements SysLogService {

	@Autowired
	private SysLogRepository sysLog;

	@Override
	public boolean createSysLog(SysLogInfo log) {
		return this.sysLog.save(log);
	}
	
	@Override
	public SearchResult<SysLogInfo> findLogsList(MODULE_LOG moduleLog,String operType,Long userId,String operId,String keys,Date startTime,Date endTime,int startIndex,int maxResults ){
		Search search=new Search(SysLogInfo.class);
		if(moduleLog!=null){
			search.addFilterEqual("moduleLog", moduleLog);
		}
		if(StringUtils.isNotEmpty(operType)){
			search.addFilterEqual("operType", OPER_TYPE.valueOf(operType));
		}
		if(StringUtils.isNotEmpty(operId)){
			search.addFilterEqual("operId", operId);
		}
		if(userId!=null){
			search.addFilterEqual("userId", userId);
		}
		if(StringUtils.isNotEmpty(keys)){
			search.addFilterLike("operDesc", "%" +  keys+ "%");
		}
		if (startTime!=null) {
			search.addFilterGreaterThan("operTime", startTime);
		}
		if (endTime != null) {
			search.addFilterLessThan("operTime", endTime);
		}
		search.setFirstResult(startIndex);
		search.setMaxResults(maxResults);
		search.setSorts(Lists.newArrayList(new Sort("operTime", true)));
		return this.sysLog.searchAndCount(search);
	}
	
	@Override
	public SysLogInfo findById(Long id){
		return this.sysLog.find(id);
	}


}
